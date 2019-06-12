package org.openpaas.paasta.ondemand.service.impl;

import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.openpaas.paasta.ondemand.common.Common;
import org.openpaas.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CloudFoundryService extends Common {


    public void ServiceInstanceAppBinding(String AppId, String ServiceInstanceId, Map parameters) {
        ReactorCloudFoundryClient reactorCloudFoundryClient = cloudFoundryClient();
        reactorCloudFoundryClient.serviceBindingsV2()
                .create(CreateServiceBindingRequest.builder().applicationId(AppId)
                        .serviceInstanceId(ServiceInstanceId).parameters(parameters).build()).block();
    }

}
