package org.openpaas.paasta.ondemand.service.impl;



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

import java.util.List;
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

    private String job_started = "started";
    private String job_detached = "detached";
    ReentrantLock lock = new ReentrantLock();
    @Autowired
    OnDemandDeploymentService onDemandDeploymentService;

    @Autowired
    JpaServiceInstanceRepository jpaServiceInstanceRepository;

    @Override
    public JpaServiceInstance createServiceInstance(CreateServiceInstanceRequest request) throws ServiceBrokerException {
        JpaServiceInstance jpaServiceInstance = new JpaServiceInstance(request);
        jpaServiceInstanceRepository.save(jpaServiceInstance);
        try {
            logger.info("name : " + deployment_name);
            logger.info("name : " + instance_name);
            List<DeploymentInstance> deploymentInstances = onDemandDeploymentService.getVmInstance(deployment_name,instance_name);
            if(deploymentInstances == null){
                throw new ServiceBrokerException(deployment_name + "is Working");
            }
            List<DeploymentInstance> startedDeploymentInstances = deploymentInstances.stream().filter((x) -> x.getState().equals(job_started) && x.getJobState().equals("running")).collect(Collectors.toList());
//            for(DeploymentInstance dep : startedDeploymentInstances){
//                if(jpaServiceInstanceRepository.findByVmInstanceIdAndOrganizationGuid(dep.getId(),request.getOrganizationGuid()) == null && jpaServiceInstanceRepository.findByVmInstanceIdAndOrganizationGuid("preparing",request.getOrganizationGuid()) == null){
//                    jpaServiceInstance.setVmInstanceId(dep.getId());
//                    jpaServiceInstance.setDashboardUrl(dep.getIps().substring(1,dep.getIps().length()-1));
//                    jpaServiceInstanceRepository.save(jpaServiceInstance);
//                    jpaServiceInstance.withAsync(false);
//                    logger.info("서비스 인스턴스 생성");
//                    return jpaServiceInstance;
//                }
//            }
            for(DeploymentInstance dep : startedDeploymentInstances){
                if(jpaServiceInstanceRepository.findByVmInstanceId(dep.getId()) == null){
                    jpaServiceInstance.setVmInstanceId(dep.getId());
                    jpaServiceInstance.setDashboardUrl(dep.getIps().substring(1,dep.getIps().length()-1));
                    jpaServiceInstanceRepository.save(jpaServiceInstance);
                    jpaServiceInstance.withAsync(false);
                    logger.info("서비스 인스턴스 생성");
                    return jpaServiceInstance;
                }
            }
            logger.info(deployment_name + "LOCK CHECKING!!!");
            //여기 지나치면 무조건 생성또는 시작해야하기 때문에 deployment 작업 여부 조회해야함
            if(onDemandDeploymentService.getLock(deployment_name)){
                throw new ServiceBrokerException(deployment_name + "is Working");
            }
            List<DeploymentInstance> detachedDeploymentInstances = deploymentInstances.stream().filter(x -> x.getState().equals(job_detached)).collect(Collectors.toList());
            String taskID = "";
            for(DeploymentInstance dep : detachedDeploymentInstances){
                onDemandDeploymentService.updateInstanceState(deployment_name, instance_name, dep.getId(), BoshDirector.INSTANCE_STATE_START);
                while(true){
                    Thread.sleep(1000);
                    taskID = onDemandDeploymentService.getTaskID(deployment_name);
                    if(taskID != null){
                        logger.info("taskID : "+ taskID);
                        break;
                    }
                }
                String ips ="";
                while(true) {
                    ips = onDemandDeploymentService.getStartInstanceIPS(taskID, instance_name, dep.getId());
                    if(ips != null){
                        break;
                    }
                    Thread.sleep(1000);
                }
                jpaServiceInstance.setVmInstanceId(dep.getId());
                jpaServiceInstance.setDashboardUrl(ips);
                jpaServiceInstanceRepository.save(jpaServiceInstance);
                jpaServiceInstance.withAsync(true);
                return jpaServiceInstance;
            }

            onDemandDeploymentService.createInstance(deployment_name, instance_name);
            while(true){
                Thread.sleep(1000);
                taskID = onDemandDeploymentService.getTaskID(deployment_name);
                if(taskID != null){
                    logger.info("taskID : "+ taskID);
                    break;
                }
            }
            String ips ="";
            while(true) {
                ips = onDemandDeploymentService.getUpdateInstanceIPS(taskID);
                if(ips != null){
                    break;
                }
                Thread.sleep(1000);
            }
            jpaServiceInstance.setDashboardUrl(ips);
            jpaServiceInstance.setVmInstanceId("preparing");
            jpaServiceInstanceRepository.save(jpaServiceInstance);
            jpaServiceInstance.withAsync(true);
            return jpaServiceInstance;
        } catch (Exception e) {
            throw new ServiceBrokerException(e.getMessage());
        }
    }

    @Override
    public ServiceInstance  updateServiceInstance(UpdateServiceInstanceRequest request)  throws ServiceInstanceUpdateNotSupportedException, ServiceBrokerException, ServiceInstanceDoesNotExistException {
        logger.info("=====================================================================");
        logger.info("update instance"+ request.toString());
        logger.info("=====================================================================");

        throw new OndemandServiceException("Not Supported");
    }

    @Override
    public ServiceInstance deleteServiceInstance(DeleteServiceInstanceRequest request) {
        logger.info("delete service_instance : " + request.getServiceInstanceId());
        JpaServiceInstance instance = jpaServiceInstanceRepository.findByServiceInstanceId(request.getServiceInstanceId());
        logger.info(instance.getVmInstanceId());
        jpaServiceInstanceRepository.delete(instance);
        if(instance.getVmInstanceId() != null && !jpaServiceInstanceRepository.existsAllByVmInstanceId(instance.getVmInstanceId())){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            CompletableFuture.runAsync(() -> {
                logger.info("****************락 확인*****************");
                lock.lock();
                logger.info("****************락 진입*****************");
                while(true){
                    if(jpaServiceInstanceRepository.existsAllByVmInstanceId(instance.getVmInstanceId())){
                        break;
                    }
                    if(onDemandDeploymentService.getLock(deployment_name)){
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    onDemandDeploymentService.updateInstanceState(deployment_name, instance_name, instance.getVmInstanceId(), BoshDirector.INSTANCE_STATE_DETACHED);
                    logger.info("VM DETACHED SUCCEED : VM_ID : " + instance.getVmInstanceId());
                    break;
                }
                logger.info("****************락 언락*****************");
                lock.unlock();
            },executor);

        }
        return instance;
    }

    @Override
    public JpaServiceInstance getServiceInstance(String Instanceid) {
        return jpaServiceInstanceRepository.findByServiceInstanceId(Instanceid);
    }

    @Override
    public JpaServiceInstance getOperationServiceInstance(String Instanceid) {
        if(onDemandDeploymentService.runningTask(deployment_name)){
            JpaServiceInstance instance = new JpaServiceInstance();
            List<DeploymentInstance> deploymentInstances = onDemandDeploymentService.getVmInstance(deployment_name,instance_name);
            List<DeploymentInstance> startedDeploymentInstances = deploymentInstances.stream().filter((x) -> x.getState().equals(job_started) && x.getJobState().equals("running")).collect(Collectors.toList());
            for(DeploymentInstance dep : startedDeploymentInstances){
                logger.info(dep.getId());
                if(!jpaServiceInstanceRepository.existsAllByVmInstanceId(dep.getId())){
                    instance = jpaServiceInstanceRepository.findByServiceInstanceId(Instanceid);
                    instance.setVmInstanceId(dep.getId());
                    jpaServiceInstanceRepository.save(instance);
                }
            }
            return instance;
        }
        logger.info("인스턴스 생성중");
        return null;
    }


}
