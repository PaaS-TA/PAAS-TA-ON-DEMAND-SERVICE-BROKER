package org.openpaas.paasta.ondemand.service.impl;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openpaas.paasta.bosh.director.BoshDirector;
import org.openpaas.paasta.ondemand.exception.OndemandServiceException;
import org.openpaas.paasta.ondemand.model.DeploymentInstance;
import org.openpaas.paasta.ondemand.model.JpaServiceInstance;
import org.openpaas.paasta.ondemand.repo.JpaServiceInstanceRepository;
import org.openpaas.servicebroker.exception.ServiceBrokerException;
import org.openpaas.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.openpaas.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.openpaas.servicebroker.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openpaas.servicebroker.service.ServiceInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


@Service
public class OnDemandInstanceService implements ServiceInstanceService {

    private static final Logger logger = LoggerFactory.getLogger(OnDemandInstanceService.class);

    @Value("${bosh.deployment_name}")
    public String deployment_name;

    @Value("${bosh.instance_name}")
    public String instance_name;

    @Value("${serviceDefinition.org_limitation}")
    public int org_limitation;

    @Value("${serviceDefinition.space_limitation}")
    public int space_limitation;

    private int unlimited = -1;

    ObjectMapper objectMapper = new ObjectMapper();

    ReentrantLock lock = new ReentrantLock();
    @Autowired
    OnDemandDeploymentService onDemandDeploymentService;

    @Autowired
    JpaServiceInstanceRepository jpaServiceInstanceRepository;

    @Autowired
    CloudFoundryService cloudFoundryService;

    @Override
    public JpaServiceInstance createServiceInstance(CreateServiceInstanceRequest request) throws ServiceBrokerException {
        logger.info("name : " + deployment_name);
        logger.info("name : " + instance_name);
        JpaServiceInstance jpaServiceInstance = new JpaServiceInstance(request);
        jpaServiceInstanceRepository.save(jpaServiceInstance);
        try {
            if (jpaServiceInstanceRepository.findAllByOrganizationGuid(request.getOrganizationGuid()).size() > org_limitation && org_limitation != unlimited) {
                throw new OndemandServiceException("Currently, only " + org_limitation + " service instances can be created in this organization.");
            }
            if (jpaServiceInstanceRepository.findAllBySpaceGuid(request.getSpaceGuid()).size() > space_limitation && space_limitation != unlimited) {
                throw new OndemandServiceException("Currently, only " + space_limitation + " service instances can be created in this space.");
            }
            List<DeploymentInstance> deploymentInstances = onDemandDeploymentService.getVmInstance(deployment_name, instance_name);
            if (deploymentInstances == null) {
                throw new ServiceBrokerException(deployment_name + " is Working");
            }
            List<DeploymentInstance> startedDeploymentInstances = deploymentInstances.stream().filter((x) -> x.getState().equals(BoshDirector.INSTANCE_STATE_START) && x.getJobState().equals("running")).collect(Collectors.toList());
            for(DeploymentInstance dep : startedDeploymentInstances){
                if(jpaServiceInstanceRepository.findByVmInstanceId(dep.getId()) == null){
                    jpaServiceInstance.setVmInstanceId(dep.getId());
                    jpaServiceInstance.setDashboardUrl(dep.getIps().substring(1,dep.getIps().length()-1));
                    jpaServiceInstanceRepository.save(jpaServiceInstance);
                    jpaServiceInstance.withAsync(true);
                    cloudFoundryService.SecurityGurop(request.getSpaceGuid(), jpaServiceInstance.getDashboardUrl());
                    logger.info("서비스 인스턴스 생성");
                    return jpaServiceInstance;
                }
            }
            logger.info("LOCK CHECKING!!!");
            //여기 지나치면 무조건 생성또는 시작해야하기 때문에 deployment 작업 여부 조회해야함
            if (onDemandDeploymentService.getLock(deployment_name)) {
                throw new ServiceBrokerException(deployment_name + " is Working");
            }
            List<DeploymentInstance> detachedDeploymentInstances = deploymentInstances.stream().filter(x -> x.getState().equals(BoshDirector.INSTANCE_STATE_DETACHED)).collect(Collectors.toList());
            String taskID = "";
            for (DeploymentInstance dep : detachedDeploymentInstances) {
                onDemandDeploymentService.updateInstanceState(deployment_name, instance_name, dep.getId(), BoshDirector.INSTANCE_STATE_START);
                while (true) {
                    Thread.sleep(1000);
                    taskID = onDemandDeploymentService.getTaskID(deployment_name);
                    if (taskID != null) {
                        logger.info("taskID : " + taskID);
                        break;
                    }
                }
                String ips = "";
                while (true) {
                    ips = onDemandDeploymentService.getStartInstanceIPS(taskID, instance_name, dep.getId());
                    if (ips != null) {
                        break;
                    }
                    Thread.sleep(1000);
                }
                jpaServiceInstance.setVmInstanceId(dep.getId());
                jpaServiceInstance.setDashboardUrl(ips);
                jpaServiceInstanceRepository.save(jpaServiceInstance);
                jpaServiceInstance.withAsync(true);
                cloudFoundryService.SecurityGurop(request.getSpaceGuid(), jpaServiceInstance.getDashboardUrl());
                return jpaServiceInstance;
            }

            onDemandDeploymentService.createInstance(deployment_name, instance_name);
            while (true) {
                Thread.sleep(1000);
                taskID = onDemandDeploymentService.getTaskID(deployment_name);
                if (taskID != null) {
                    logger.info("taskID : " + taskID);
                    break;
                }
            }
            String ips = "";
            while (true) {
                ips = onDemandDeploymentService.getUpdateInstanceIPS(taskID);
                if (ips != null) {
                    break;
                }
                Thread.sleep(1000);

            }
            String instanceId = "";
            while (true) {
                instanceId = onDemandDeploymentService.getUpdateVMInstanceID(taskID, instance_name);
                if (instanceId != null) {
                    break;
                }
                Thread.sleep(1000);
            }
            jpaServiceInstance.setDashboardUrl(ips);
            jpaServiceInstance.setVmInstanceId(instanceId);
            jpaServiceInstanceRepository.save(jpaServiceInstance);
            jpaServiceInstance.withAsync(true);
            cloudFoundryService.SecurityGurop(request.getSpaceGuid(), jpaServiceInstance.getDashboardUrl());
            return jpaServiceInstance;
        } catch (Exception e) {
            throw new ServiceBrokerException(e.getMessage());

        }
    }

    @Override
    public ServiceInstance updateServiceInstance(UpdateServiceInstanceRequest request) throws ServiceInstanceUpdateNotSupportedException, ServiceBrokerException, ServiceInstanceDoesNotExistException {
        throw new ServiceBrokerException("Not Supported");
    }

    @Override
    public ServiceInstance deleteServiceInstance(DeleteServiceInstanceRequest request) {
        JpaServiceInstance instance = jpaServiceInstanceRepository.findByServiceInstanceId(request.getServiceInstanceId());
        jpaServiceInstanceRepository.delete(instance);
        if (instance.getVmInstanceId() != null && !jpaServiceInstanceRepository.existsAllByVmInstanceId(instance.getVmInstanceId())) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            CompletableFuture.runAsync(() -> {
                lock.lock();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
                while (true) {
                    if (onDemandDeploymentService.getLock(deployment_name)) {
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            logger.error(e.getMessage());
                        }
                        continue;
                    }
                    onDemandDeploymentService.updateInstanceState(deployment_name, instance_name, instance.getVmInstanceId(), BoshDirector.INSTANCE_STATE_DETACHED);
                    logger.info("VM DETACHED SUCCEED : VM_ID : " + instance.getVmInstanceId());

                    break;
                }
                lock.unlock();
            }, executor);

        }
        return instance;
    }

    @Override
    public JpaServiceInstance getServiceInstance(String Instanceid) {
        return jpaServiceInstanceRepository.findByServiceInstanceId(Instanceid);
    }

    @Override
    public JpaServiceInstance getOperationServiceInstance(String Instanceid) {
        JpaServiceInstance instance = jpaServiceInstanceRepository.findByServiceInstanceId(Instanceid);
        if (onDemandDeploymentService.runningTask(deployment_name, instance)) {
            logger.info("인스턴스 생성완료");
            ExecutorService executor = Executors.newSingleThreadExecutor();
            CompletableFuture.runAsync(() -> {
                try {
                    if (instance.getAppGuid() != null) {
                        Thread.sleep(10000);
                        cloudFoundryService.ServiceInstanceAppBinding(instance.getAppGuid(), instance.getServiceInstanceId(), (Map) this.objectMapper.readValue(instance.getApp_parameter(), Map.class));
                    }
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                } catch (JsonParseException e) {
                    logger.error(e.getMessage());
                } catch (JsonMappingException e) {
                    logger.error(e.getMessage());
                } catch (IOException e) {
                    logger.error(e.getMessage());
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }, executor);
            return instance;
        }
        logger.info("인스턴스 생성중");
        return null;
    }


}
