package org.openpaas.paasta.ondemand.test;


import model.DeploymentInstanceModel;
import model.ServiceInstanceModel;
import model.ServiceInstanceRequestModel;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.openpaas.paasta.bosh.director.BoshDirector;
import org.openpaas.paasta.ondemand.model.DeploymentInstance;
import org.openpaas.paasta.ondemand.model.JpaServiceInstance;
import org.openpaas.paasta.ondemand.repo.JpaServiceInstanceRepository;
import org.openpaas.paasta.ondemand.service.impl.CloudFoundryService;
import org.openpaas.paasta.ondemand.service.impl.OnDemandDeploymentService;
import org.openpaas.paasta.ondemand.service.impl.OnDemandInstanceService;
import org.openpaas.servicebroker.exception.ServiceBrokerException;
import org.openpaas.servicebroker.model.CreateServiceInstanceRequest;
import org.openpaas.servicebroker.model.DeleteServiceInstanceRequest;
import org.openpaas.servicebroker.model.ServiceInstance;
import org.openpaas.servicebroker.model.UpdateServiceInstanceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OnDemandInstanceServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @InjectMocks
    OnDemandInstanceService onDemandInstanceService;

    @Mock
    OnDemandDeploymentService onDemandDeploymentService;

    @Mock
    JpaServiceInstanceRepository jpaServiceInstanceRepository;

    @Mock
    CloudFoundryService cloudFoundryService;

    @Mock
    private MockRestServiceServer mockServer;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Executor serviceBExecutorService;
    // My class in which I want to inject the mocks
    @InjectMocks
    private CompletableFuture service;

    @Rule
    public InterruptedException interruptedException = new InterruptedException();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockServer = MockRestServiceServer.createServer(restTemplate);
        service = new CompletableFuture();
        ReflectionTestUtils.setField(onDemandInstanceService, "deployment_name", "deployment_name");
        ReflectionTestUtils.setField(onDemandInstanceService, "instance_name", "instance_name");
        ReflectionTestUtils.setField(onDemandInstanceService, "org_limitation", -1);
        ReflectionTestUtils.setField(onDemandInstanceService, "space_limitation", -1);
        print();
    }

    private void print() {
        logger.info(onDemandInstanceService.instance_name);
        logger.info(onDemandInstanceService.deployment_name);
    }

    @Test
    public void updateServiceInstanceTest() throws Exception {
        UpdateServiceInstanceRequest request = ServiceInstanceRequestModel.getUpdateServiceInstanceRequest();
        assertThatThrownBy(() -> onDemandInstanceService.updateServiceInstance(request))
                .isInstanceOf(ServiceBrokerException.class).hasMessageContaining("Not Supported");
    }

    @Test
    public void getServiceInstanceTest() throws Exception {
        JpaServiceInstance serviceInstance = ServiceInstanceModel.getJpaServiceInstance();
        when(jpaServiceInstanceRepository.findByServiceInstanceId(anyString())).thenReturn(serviceInstance);
        ServiceInstance result = onDemandInstanceService.getServiceInstance(serviceInstance.getServiceInstanceId());
        assertThat(result.getServiceInstanceId(), is(serviceInstance.getServiceInstanceId()));
        assertThat(result.getServiceDefinitionId(), is(serviceInstance.getServiceDefinitionId()));
        assertThat(result.getOrganizationGuid(), is(serviceInstance.getOrganizationGuid()));
        assertThat(result.getPlanId(), is(serviceInstance.getPlanId()));
        assertThat(result.getSpaceGuid(), is(serviceInstance.getSpaceGuid()));
    }

    //org 할당된 Service Instance 초과될경우
    @Test
    public void createServiceInstanceTest_1() throws Exception {
        CreateServiceInstanceRequest request = ServiceInstanceRequestModel.getCreateServiceInstanceRequest();
        onDemandInstanceService.org_limitation = -2;
        assertThatThrownBy(() -> onDemandInstanceService.createServiceInstance(request))
                .isInstanceOf(ServiceBrokerException.class).hasMessageContaining("Currently, only -2 service instances can be created in this organization.");
    }

    //space 할당된 Service Instance 초과될경우
    @Test
    public void createServiceInstanceTest_2() throws Exception {
        CreateServiceInstanceRequest request = ServiceInstanceRequestModel.getCreateServiceInstanceRequest();
        onDemandInstanceService.space_limitation = -2;
        assertThatThrownBy(() -> onDemandInstanceService.createServiceInstance(request))
                .isInstanceOf(ServiceBrokerException.class).hasMessageContaining("Currently, only -2 service instances can be created in this space.");
    }

    //getVmInstance == null
    @Test
    public void createServiceInstanceTest_3() throws Exception {
        CreateServiceInstanceRequest request = ServiceInstanceRequestModel.getCreateServiceInstanceRequest();
        when(onDemandDeploymentService.getVmInstance("deployment_name", "instance_name")).thenReturn(null);
        assertThatThrownBy(() -> onDemandInstanceService.createServiceInstance(request))
                .isInstanceOf(ServiceBrokerException.class).hasMessageContaining("deployment_name is Working");
    }

    //findByVmInstanceId == null
    @Test
    public void createServiceInstanceTest_4() throws Exception {
        CreateServiceInstanceRequest request = ServiceInstanceRequestModel.getCreateServiceInstanceRequest();
        List<DeploymentInstance> getVmInstance = new ArrayList<>();
        getVmInstance.add(DeploymentInstanceModel.getDeploymentInstance());
        when(onDemandDeploymentService.getVmInstance("deployment_name", "instance_name")).thenReturn(getVmInstance);
        when(jpaServiceInstanceRepository.findByVmInstanceId(getVmInstance.get(0).getId())).thenReturn(null);
        JpaServiceInstance result = onDemandInstanceService.createServiceInstance(request);
        assertThat(result.getVmInstanceId(), is(getVmInstance.get(0).getId()));
        assertThat(result.getDashboardUrl(), is(getVmInstance.get(0).getIps().substring(1,getVmInstance.get(0).getIps().length()-1)));
    }

    //getLock == true
    @Test
    public void createServiceInstanceTest_4_1() throws Exception {
        CreateServiceInstanceRequest request = ServiceInstanceRequestModel.getCreateServiceInstanceRequest();
        List<DeploymentInstance> getVmInstance = new ArrayList<>();
        getVmInstance.add(DeploymentInstanceModel.getDeploymentInstance());
        JpaServiceInstance jpaServiceInstance = new JpaServiceInstance();
        when(onDemandDeploymentService.getVmInstance("deployment_name", "instance_name")).thenReturn(getVmInstance);
        when(jpaServiceInstanceRepository.findByVmInstanceId(getVmInstance.get(0).getId())).thenReturn(jpaServiceInstance);
        when(onDemandDeploymentService.getLock("deployment_name")).thenReturn(true);
        assertThatThrownBy(() -> onDemandInstanceService.createServiceInstance(request))
                .isInstanceOf(ServiceBrokerException.class).hasMessageContaining("deployment_name is Working");
    }

    //getLock == true
    @Test
    public void createServiceInstanceTest_5() throws Exception {
        CreateServiceInstanceRequest request = ServiceInstanceRequestModel.getCreateServiceInstanceRequest();
        when(onDemandDeploymentService.getLock("deployment_name")).thenReturn(true);
        assertThatThrownBy(() -> onDemandInstanceService.createServiceInstance(request))
                .isInstanceOf(ServiceBrokerException.class).hasMessageContaining("deployment_name is Working");
    }

    //Detach VM Start Test
    @Test
    public void createServiceInstanceTest_6() throws Exception {
        CreateServiceInstanceRequest request = ServiceInstanceRequestModel.getCreateServiceInstanceRequest();
        List<DeploymentInstance> getVmInstance = new ArrayList<>();
        getVmInstance.add(DeploymentInstanceModel.getDeploymentDetachedInstance());
        String taskId = anyString();
        String ips = anyString();
        when(onDemandDeploymentService.getVmInstance("deployment_name", "instance_name")).thenReturn(getVmInstance);
        when(onDemandDeploymentService.getLock("deployment_name")).thenReturn(false);
        when(onDemandDeploymentService.getTaskID("deployment_name")).thenReturn(taskId);
        when(onDemandDeploymentService.getStartInstanceIPS(taskId,"instance_name",getVmInstance.get(0).getId())).thenReturn(ips);
        JpaServiceInstance result = onDemandInstanceService.createServiceInstance(request);
        assertThat(result.getVmInstanceId(), is(getVmInstance.get(0).getId()));
        assertThat(result.getDashboardUrl(), is(ips));
    }

    //Detach VM Start Sleep Test
    @Test
    public void createServiceInstanceTest_6_1() throws Exception {
        CreateServiceInstanceRequest request = ServiceInstanceRequestModel.getCreateServiceInstanceRequest();
        List<DeploymentInstance> getVmInstance = new ArrayList<>();
        getVmInstance.add(DeploymentInstanceModel.getDeploymentDetachedInstance());
        when(onDemandDeploymentService.getVmInstance("deployment_name", "instance_name")).thenReturn(getVmInstance);
        when(onDemandDeploymentService.getLock("deployment_name")).thenReturn(false);
        when(onDemandDeploymentService.getTaskID("deployment_name")).thenReturn(null);
    }

    //Detach VM Start Sleep Test
    @Test
    public void createServiceInstanceTest_6_2() throws Exception {
        CreateServiceInstanceRequest request = ServiceInstanceRequestModel.getCreateServiceInstanceRequest();
        List<DeploymentInstance> getVmInstance = new ArrayList<>();
        getVmInstance.add(DeploymentInstanceModel.getDeploymentDetachedInstance());
        when(onDemandDeploymentService.getVmInstance("deployment_name", "instance_name")).thenReturn(getVmInstance);
        when(onDemandDeploymentService.getLock("deployment_name")).thenReturn(false);
        when(onDemandDeploymentService.getTaskID("deployment_name")).thenReturn(null);
    }

    //Detach VM Instance Create Test
    @Test
    public void createServiceInstanceTest_7() throws Exception {
        CreateServiceInstanceRequest request = ServiceInstanceRequestModel.getCreateServiceInstanceRequest();
        List<DeploymentInstance> getVmInstance = new ArrayList<>();
        getVmInstance.add(DeploymentInstanceModel.getDeploymentEmptyInstance());
        String taskId = "test taskId";
        String ips = "test Ips";
        String instance_id = "test instance_id";
        when(onDemandDeploymentService.getVmInstance("deployment_name", "instance_name")).thenReturn(getVmInstance);
        when(onDemandDeploymentService.getLock("deployment_name")).thenReturn(false);
        Mockito.doCallRealMethod().when(onDemandDeploymentService).createInstance("deployment_name","instance_name");
        onDemandDeploymentService.createInstance("deployment_name","instance_name");
        when(onDemandDeploymentService.getTaskID("deployment_name")).thenReturn(taskId);
        when(onDemandDeploymentService.getUpdateInstanceIPS(taskId)).thenReturn(ips);
        when(onDemandDeploymentService.getUpdateVMInstanceID(taskId,"instance_name")).thenReturn(instance_id);
        JpaServiceInstance result = onDemandInstanceService.createServiceInstance(request);
        assertThat(result.getVmInstanceId(), is(instance_id));
        assertThat(result.getDashboardUrl(), is(ips));
    }

    //Detach VM Instance Create Sleep Test
    @Test
    public void createServiceInstanceTest_7_1() throws Exception {
        CreateServiceInstanceRequest request = ServiceInstanceRequestModel.getCreateServiceInstanceRequest();
        List<DeploymentInstance> getVmInstance = new ArrayList<>();
        getVmInstance.add(DeploymentInstanceModel.getDeploymentEmptyInstance());
        when(onDemandDeploymentService.getVmInstance("deployment_name", "instance_name")).thenReturn(getVmInstance);
        when(onDemandDeploymentService.getLock("deployment_name")).thenReturn(false);
        Mockito.doCallRealMethod().when(onDemandDeploymentService).createInstance("deployment_name","instance_name");
        onDemandDeploymentService.createInstance("deployment_name","instance_name");
        when(onDemandDeploymentService.getTaskID("deployment_name")).thenReturn(null);
    }

    //Detach VM Instance Create Sleep Test
    @Test
    public void createServiceInstanceTest_7_2() throws Exception {
        CreateServiceInstanceRequest request = ServiceInstanceRequestModel.getCreateServiceInstanceRequest();
        List<DeploymentInstance> getVmInstance = new ArrayList<>();
        getVmInstance.add(DeploymentInstanceModel.getDeploymentEmptyInstance());
        String taskId = "test taskId";
        when(onDemandDeploymentService.getVmInstance("deployment_name", "instance_name")).thenReturn(getVmInstance);
        when(onDemandDeploymentService.getLock("deployment_name")).thenReturn(false);
        Mockito.doCallRealMethod().when(onDemandDeploymentService).createInstance("deployment_name","instance_name");
        onDemandDeploymentService.createInstance("deployment_name","instance_name");
        when(onDemandDeploymentService.getTaskID("deployment_name")).thenReturn(taskId);
        when(onDemandDeploymentService.getUpdateInstanceIPS(taskId)).thenReturn(null);
    }

    //Detach VM Instance Create Test
    @Test
    public void deleteServiceInstanceTest_1() throws Exception {
        DeleteServiceInstanceRequest request = ServiceInstanceRequestModel.getDeleteServiceInstanceRequest();
        JpaServiceInstance jpaServiceInstance = new JpaServiceInstance();
        when(jpaServiceInstanceRepository.findByServiceInstanceId(request.getServiceInstanceId())).thenReturn(jpaServiceInstance);
        jpaServiceInstance.setVmInstanceId(null);
        ServiceInstance result = onDemandInstanceService.deleteServiceInstance(request);
        assertThat(result.getServiceInstanceId(), is(jpaServiceInstance.getServiceInstanceId()));
    }


    //Detach VM Instance Create Test
    @Test
    public void deleteServiceInstanceTest_2() throws Exception {
        DeleteServiceInstanceRequest request = ServiceInstanceRequestModel.getDeleteServiceInstanceRequest();
        JpaServiceInstance jpaServiceInstance = new JpaServiceInstance();
        when(jpaServiceInstanceRepository.findByServiceInstanceId(request.getServiceInstanceId())).thenReturn(jpaServiceInstance);
        when(jpaServiceInstanceRepository.existsAllByVmInstanceId(request.getServiceInstanceId())).thenReturn(true);
        when(onDemandDeploymentService.getLock("deployment_name")).thenReturn(false);
        jpaServiceInstance.setVmInstanceId(anyString());
        ServiceInstance result = onDemandInstanceService.deleteServiceInstance(request);
        assertThat(result.getServiceInstanceId(), is(jpaServiceInstance.getServiceInstanceId()));
    }

    @Test
    public void deleteServiceInstanceTest_2_1() throws Exception {
        DeleteServiceInstanceRequest request = ServiceInstanceRequestModel.getDeleteServiceInstanceRequest();
        JpaServiceInstance jpaServiceInstance = new JpaServiceInstance();
        when(jpaServiceInstanceRepository.findByServiceInstanceId(request.getServiceInstanceId())).thenReturn(jpaServiceInstance);
        when(jpaServiceInstanceRepository.existsAllByVmInstanceId(request.getServiceInstanceId())).thenReturn(true);
        when(onDemandDeploymentService.getLock("deployment_name")).thenReturn(false);
        jpaServiceInstance.setVmInstanceId(anyString());
        ServiceInstance result = onDemandInstanceService.deleteServiceInstance(request);
    }

    //Detach VM Instance Create Test
    @Test
    public void deleteServiceInstanceTest_3() throws Exception {
        DeleteServiceInstanceRequest request = ServiceInstanceRequestModel.getDeleteServiceInstanceRequest();
        JpaServiceInstance jpaServiceInstance = new JpaServiceInstance();
        when(jpaServiceInstanceRepository.findByServiceInstanceId(request.getServiceInstanceId())).thenReturn(jpaServiceInstance);
        when(jpaServiceInstanceRepository.existsAllByVmInstanceId(request.getServiceInstanceId())).thenReturn(true);
        when(onDemandDeploymentService.getLock("deployment_name")).thenReturn(true);
        jpaServiceInstance.setVmInstanceId(anyString());
        ServiceInstance result = onDemandInstanceService.deleteServiceInstance(request);
        assertThat(result.getServiceInstanceId(), is(jpaServiceInstance.getServiceInstanceId()));
    }


    //Detach VM Instance Create Test
    @Test
    public void getOperationServiceInstanceTest_1() throws Exception {
        JpaServiceInstance jpaServiceInstance = new JpaServiceInstance();
        String InstacneId = "Instance_id";
        when(jpaServiceInstanceRepository.findByServiceInstanceId(InstacneId)).thenReturn(jpaServiceInstance);
        when(onDemandDeploymentService.runningTask("deployment_name",jpaServiceInstance)).thenReturn(false);
        JpaServiceInstance result = onDemandInstanceService.getOperationServiceInstance(InstacneId);
        jpaServiceInstance = null;
        assertThat(result, is(jpaServiceInstance));
    }

    //Detach VM Instance Create Test
    @Test
    public void getOperationServiceInstanceTest_2() throws Exception {
        JpaServiceInstance jpaServiceInstance = new JpaServiceInstance();
        String InstacneId = "Instance_id";
        jpaServiceInstance.setAppGuid("app_guid");
        when(jpaServiceInstanceRepository.findByServiceInstanceId(InstacneId)).thenReturn(jpaServiceInstance);
        when(onDemandDeploymentService.runningTask("deployment_name",jpaServiceInstance)).thenReturn(true);
        ExecutorService service = Executors.newSingleThreadExecutor();
        CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> System.out.println("running async task"), service);
        JpaServiceInstance result = onDemandInstanceService.getOperationServiceInstance(InstacneId);
        assertThat(result, is(jpaServiceInstance));
    }





}
