package org.openpaas.paasta.ondemand.test;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openpaas.paasta.bosh.director.BoshDirector;
import org.openpaas.paasta.ondemand.config.BoshConfig;
import org.openpaas.paasta.ondemand.model.DeploymentInstance;
import org.openpaas.paasta.ondemand.service.impl.OnDemandDeploymentService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OnDemandDeploymentServiceTest {

    @InjectMocks
    OnDemandDeploymentService onDemandDeploymentService;

    @Spy
    BoshConfig boshConfig;

    @Mock
    BoshDirector boshDirector;

    @Mock
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
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
    public void createServiceInstanceBinding_test1() throws Exception {
        String task = "";
        List<Map> result = new ArrayList<>();
//        when(boshDirector.getListDetailOfInstances("deployment_name")).thenReturn(task);
//        when(boshDirector.getResultRetrieveTasksLog(task)).thenReturn(result);
        List<DeploymentInstance> deploymentInstances = onDemandDeploymentService.getVmInstance("deployment_name","instance_name");
//        assertThat(deploymentInstances, null);
    }
}
