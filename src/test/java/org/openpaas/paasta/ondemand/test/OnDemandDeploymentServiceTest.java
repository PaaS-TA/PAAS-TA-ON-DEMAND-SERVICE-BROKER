package org.openpaas.paasta.ondemand.test;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import org.mockito.*;
import org.openpaas.paasta.bosh.director.BoshDirector;
import org.openpaas.paasta.ondemand.config.BoshConfig;
import org.openpaas.paasta.ondemand.model.DeploymentInstance;
import org.openpaas.paasta.ondemand.model.JpaServiceInstance;
import org.openpaas.paasta.ondemand.service.impl.OnDemandDeploymentService;
import org.openpaas.servicebroker.exception.ServiceBrokerException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.is;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OnDemandDeploymentServiceTest {

    @InjectMocks
    OnDemandDeploymentService onDemandDeploymentService;

    @Mock
    BoshConfig boshConfig;

    @Mock
    BoshDirector boshDirector;

    @Mock
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OAuth2AccessToken oAuth2AccessToken = null;
        mockServer = MockRestServiceServer.createServer(restTemplate);
        ReflectionTestUtils.setField(boshConfig, "client_id", "client_id");
        ReflectionTestUtils.setField(boshConfig, "client_secret", "client_secret");
        ReflectionTestUtils.setField(boshConfig, "url", "https://11.111.11.111");
        ReflectionTestUtils.setField(boshConfig, "oauth_url", "https://11.11.11.111");
        ReflectionTestUtils.setField(boshConfig, "deployment_name", "deployment_name");
        ReflectionTestUtils.setField(boshConfig, "instance_name", "instance_name");
        onDemandDeploymentService.setBoshDirector(boshDirector);
    }

    @Test
    public void getVmInstance_test1() throws Exception {
        String task = "";
        List<Map> result = new ArrayList<>();
        when(boshDirector.getListDetailOfInstances("deployment_name")).thenReturn(task);
        when(boshDirector.getResultRetrieveTasksLog(task)).thenReturn(result);
        List<DeploymentInstance> deploymentInstances = onDemandDeploymentService.getVmInstance("deployment_name","instance_name");
        assertThat(deploymentInstances, is(result));
    }

    @Test
    public void getVmInstance_test2() throws Exception {
        String task = "";
        List<Map> result = new ArrayList<>();
        Map map = new HashMap();
        map.put("job_name","instance_name");
        result.add(map);
        when(boshDirector.getListDetailOfInstances("deployment_name")).thenReturn(task);
        when(boshDirector.getResultRetrieveTasksLog(task)).thenReturn(result);
        List<DeploymentInstance> deploymentInstances = onDemandDeploymentService.getVmInstance("deployment_name","instance_name");
        assertThat(deploymentInstances.get(0).getJobName(), is(result.get(0).get("job_name")));
    }

    //getLock_Exception TEST
    @Test
    public void getLock_test1() throws Exception {
        String locks = "";
        when(boshDirector.getListLocks()).thenReturn(locks);
        boolean result = onDemandDeploymentService.getLock("deployment_name");
        assertThat(result, is(false));
    }

    //getLock TEST false
    @Test
    public void getLock_test2() throws Exception {
        String locks = "[{\"type\":\"deployment\",\"resource\":[\"on-demand-service-broker\"],\"timeout\":\"1563324005.776951\",\"task_id\":\"939115\"},{\"type\":\"compile\",\"resource\":[\"1492\",\"ubuntu-trusty/3586.25\"],\"timeout\":\"1563324016.524488\",\"task_id\":\"939115\"},{\"type\":\"compile\",\"resource\":[\"1491\",\"ubuntu-trusty/3586.25\"],\"timeout\":\"1563324016.521792\",\"task_id\":\"939115\"},{\"type\":\"compile\",\"resource\":[\"1489\",\"ubuntu-trusty/3586.25\"],\"timeout\":\"1563324016.527235\",\"task_id\":\"939115\"},{\"type\":\"compile\",\"resource\":[\"1490\",\"ubuntu-trusty/3586.25\"],\"timeout\":\"1563324016.539023\",\"task_id\":\"939115\"}]";
        when(boshDirector.getListLocks()).thenReturn(locks);
        boolean result = onDemandDeploymentService.getLock("deployment_name");
        assertThat(result, is(false));
    }

    //getLock TEST True
    @Test
    public void getLock_test3() throws Exception {
        String locks = "[{\"type\":\"deployment\",\"resource\":[\"deployment_name\"],\"timeout\":\"1563324005.776951\",\"task_id\":\"939115\"},{\"type\":\"compile\",\"resource\":[\"1492\",\"ubuntu-trusty/3586.25\"],\"timeout\":\"1563324016.524488\",\"task_id\":\"939115\"},{\"type\":\"compile\",\"resource\":[\"1491\",\"ubuntu-trusty/3586.25\"],\"timeout\":\"1563324016.521792\",\"task_id\":\"939115\"},{\"type\":\"compile\",\"resource\":[\"1489\",\"ubuntu-trusty/3586.25\"],\"timeout\":\"1563324016.527235\",\"task_id\":\"939115\"},{\"type\":\"compile\",\"resource\":[\"1490\",\"ubuntu-trusty/3586.25\"],\"timeout\":\"1563324016.539023\",\"task_id\":\"939115\"}]";
        when(boshDirector.getListLocks()).thenReturn(locks);
        boolean result = onDemandDeploymentService.getLock("deployment_name");
        assertThat(result, is(true));
    }



    //updateInstanceState TEST True
    @Test
    public void updateInstanceState_test1() throws Exception {
        when(boshDirector.updateInstanceState("deployment_name","instance_name","instance_id","type")).thenReturn(true);
        onDemandDeploymentService.updateInstanceState("deployment_name","instance_name","instance_id","type");
    }

    //updateInstanceState TEST Exception
    @Test
    public void updateInstanceState_test2() throws Exception {
        when(boshDirector.updateInstanceState("deployment_name","instance_name","instance_id","type")).thenReturn(false);
        onDemandDeploymentService.updateInstanceState("deployment_name","instance_name","instance_id","type");
    }

    //runningTask TEST Exception
    @Test
    public void runningTask_test1() throws Exception {
        JpaServiceInstance jpaServiceInstance = new JpaServiceInstance();
        when(boshDirector.getListRunningTasks()).thenReturn(null);
        boolean result = onDemandDeploymentService.runningTask("deployment_name",jpaServiceInstance);
        assertThat(result, is(false));
    }

    //runningTask TEST
    @Test
    public void runningTask_test2() throws Exception {
        JpaServiceInstance jpaServiceInstance = new JpaServiceInstance();
        when(boshDirector.getListRunningTasks()).thenReturn(new ArrayList<>());
        boolean result = onDemandDeploymentService.runningTask("deployment_name",jpaServiceInstance);
        assertThat(result, is(true));
    }

    //runningTask TEST
    @Test
    public void runningTask_test3() throws Exception {
        JpaServiceInstance jpaServiceInstance = new JpaServiceInstance();
        Map map = new HashMap();
        map.put("deployment", "deployment_name");
        map.put("id", "instance_name");
        List<Map> param = new ArrayList<>();
        param.add(map);
        jpaServiceInstance.setTaskId(null);
        when(boshDirector.getListRunningTasks()).thenReturn(param);
        boolean result = onDemandDeploymentService.runningTask("deployment_name",jpaServiceInstance);
        assertThat(result, is(false));
    }

    //runningTask TEST True
    @Test
    public void runningTask_test4() throws Exception {
        JpaServiceInstance jpaServiceInstance = new JpaServiceInstance();
        Map map = new HashMap();
        map.put("deployment", "deployment_name");
        map.put("id", "instance_name");
        List<Map> param = new ArrayList<>();
        param.add(map);
        jpaServiceInstance.setTaskId("instance_id");
        when(boshDirector.getListRunningTasks()).thenReturn(param);
        boolean result = onDemandDeploymentService.runningTask("deployment_name",jpaServiceInstance);
        assertThat(result, is(true));
    }

}
