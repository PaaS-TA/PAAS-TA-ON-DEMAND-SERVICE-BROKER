package org.openpaas.paasta.ondemand.test;

import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.openpaas.paasta.ondemand.common.Common;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CloudFoundryServiceTest {

    @Mock
    Common common;

    @Test
    public void noName() {
        ReactorCloudFoundryClient reactorCloudFoundryClient = ReactorCloudFoundryClient.builder().connectionContext(DefaultConnectionContext.builder().apiHost("xxx.xxx.xxx.xxx").skipSslValidation(true).keepAlive(true).build()).tokenProvider(PasswordGrantTokenProvider.builder().password("password").username("username").build()).build();
        reactorCloudFoundryClient.serviceBindingsV2()
                .create(CreateServiceBindingRequest.builder().applicationId("appid")
                        .serviceInstanceId("serviceInstanceId").parameters(new HashMap<>()).build());
    }
}
