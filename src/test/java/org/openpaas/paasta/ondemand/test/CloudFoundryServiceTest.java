package org.openpaas.paasta.ondemand.test;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.applications.RestageApplicationRequest;
import org.cloudfoundry.client.v2.applications.RestageApplicationResponse;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingEntity;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingsV2;
import org.cloudfoundry.client.v2.users.Users;
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
import org.openpaas.paasta.ondemand.common.Common;
import org.openpaas.paasta.ondemand.common.PaastaConnectionContext;
import org.openpaas.paasta.ondemand.service.impl.CloudFoundryService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;


import java.util.HashMap;

import static org.mockito.Mockito.*;

@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CloudFoundryServiceTest {

    @Mock
    Common common;

    @InjectMocks
    CloudFoundryService cloudFoundryService;

    @Mock
    PaastaConnectionContext paastaConnectionContext;

    @Mock
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    ReactorCloudFoundryClient reactorCloudFoundryClient;
    DefaultConnectionContext defaultConnectionContextBuild;
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(common, "apiTarget", "https://api.xxx.xx.xx.xxx.xip.io");
        ReflectionTestUtils.setField(common, "uaaTarget", "https://uaa.xxx.xx.xx.xxx.xip.io");
        ReflectionTestUtils.setField(common, "cfskipSSLValidation", true);
        ReflectionTestUtils.setField(common, "adminUserName", "admin");
        ReflectionTestUtils.setField(common, "adminPassword", "password");
//        defaultConnectionContextBuild = DefaultConnectionContext.builder().apiHost("api.xxx.xx.xx.xxx.xip.io").skipSslValidation(true).build();
////        paastaConnectionContext = new PaastaConnectionContext(defaultConnectionContextBuild, new Date());
//        TokenProvider tokenProvider = PasswordGrantTokenProvider.builder().password("admin").username("password").build();
//        reactorCloudFoundryClient = ReactorCloudFoundryClient.builder().connectionContext(defaultConnectionContextBuild).tokenProvider(tokenProvider).build();
//
//        common.paastaConnectionContext = paastaConnectionContext;

    }

    @Test
    public void ServiceInstanceAppBinding_test() throws Exception {

//        when(common.defaultConnectionContextBuild("apiTarget", true)).thenReturn(defaultConnectionContextBuild);
//        when(common.cloudFoundryClient()).thenReturn(reactorCloudFoundryClient);
//        when(common.ContextAndTokenTimeOut(paastaConnectionContext, 10)).thenReturn(false);
//        when(common.ContextAndTokenTimeOut(paastaConnectionContext, 10)).thenReturn(false);
        //ReactorCloudFoundryClient reactorCloudFoundryClient2 = mock(ReactorCloudFoundryClient.class, RETURNS_SMART_NULLS);
        ServiceBindingsV2 serviceBindingsV2 = mock(ServiceBindingsV2.class, RETURNS_SMART_NULLS);
        ApplicationsV2 applicationsV2 = mock(ApplicationsV2.class, RETURNS_SMART_NULLS);
        when(serviceBindingsV2.create(CreateServiceBindingRequest.builder().applicationId("test")
                .serviceInstanceId("Instance_id").parameters(new HashMap<>()).build())).thenReturn(Mono.just(CreateServiceBindingResponse.builder().metadata(Metadata.builder().id("hello").build()).entity(ServiceBindingEntity.builder().name("test").build()).build()));
        when(applicationsV2.restage(RestageApplicationRequest.builder().applicationId("test").build())).thenReturn(Mono.just(RestageApplicationResponse.builder().build()));





        cloudFoundryService.ServiceInstanceAppBinding("test","Instance_id" ,new HashMap<>(), serviceBindingsV2, applicationsV2);

    }
}
