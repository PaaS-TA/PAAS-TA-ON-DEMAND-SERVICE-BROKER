package org.openpaas.paasta.ondemand.test;

import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openpaas.paasta.ondemand.common.Common;
import org.openpaas.paasta.ondemand.common.PaastaConnectionContext;
import org.openpaas.paasta.ondemand.service.impl.CloudFoundryService;
import org.openpaas.servicebroker.model.CreateServiceInstanceResponse;
import org.openpaas.servicebroker.model.ServiceInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CloudFoundryServiceTest {

    @Mock
    Common common;

    @Mock
    CloudFoundryService cloudFoundryService;

    @Mock
    PaastaConnectionContext paastaConnectionContext;


    ReactorCloudFoundryClient reactorCloudFoundryClient;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(common, "apiTarget", "apiTarget");
        ReflectionTestUtils.setField(common, "uaaTarget", "uaaTarget");
        ReflectionTestUtils.setField(common, "cfskipSSLValidation", true);
        ReflectionTestUtils.setField(common, "adminUserName", "adminUserName");
        ReflectionTestUtils.setField(common, "adminPassword", "adminPassword");
        DefaultConnectionContext defaultConnectionContextBuild = DefaultConnectionContext.builder().apiHost("xx.xx.xx.xxx").skipSslValidation(true).build();
//        paastaConnectionContext = new PaastaConnectionContext(defaultConnectionContextBuild, new Date());
        TokenProvider tokenProvider = PasswordGrantTokenProvider.builder().password("adminUserName").username("adminPassword").build();
        reactorCloudFoundryClient = ReactorCloudFoundryClient.builder().connectionContext(defaultConnectionContextBuild).tokenProvider(tokenProvider).build();
//
//        common.paastaConnectionContext = paastaConnectionContext;

    }

    @Test
    public void ServiceInstanceAppBinding_test() throws Exception {

        //when(paastaConnectionContext.getCreate_time()).thenReturn(new Date());
        when(cloudFoundryService.cloudFoundryClient()).thenReturn(reactorCloudFoundryClient);
        when(cloudFoundryService.ContextAndTokenTimeOut(paastaConnectionContext, 10)).thenReturn(false);
        cloudFoundryService.ServiceInstanceAppBinding("test","Instance_id" ,new HashMap<>());

    }
}
