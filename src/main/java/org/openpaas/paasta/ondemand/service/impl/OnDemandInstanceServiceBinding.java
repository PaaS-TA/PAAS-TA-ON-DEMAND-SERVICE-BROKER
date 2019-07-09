package org.openpaas.paasta.ondemand.service.impl;


import org.openpaas.paasta.ondemand.exception.OndemandServiceException;
import org.openpaas.paasta.ondemand.model.JpaServiceInstance;
import org.openpaas.paasta.ondemand.repo.JpaServiceInstanceRepository;
import org.openpaas.servicebroker.exception.ServiceBrokerException;
import org.openpaas.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.openpaas.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.openpaas.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.openpaas.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.openpaas.servicebroker.model.ServiceInstanceBinding;

import org.openpaas.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OnDemandInstanceServiceBinding implements ServiceInstanceBindingService {

    @Autowired
    JpaServiceInstanceRepository jpaServiceInstanceRepository;

    @Value("${instance.password}")
    public String password;

    @Value("${instance.port}")
    public int port;

    @Override
    public ServiceInstanceBinding createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {
        Map<String, Object> credentials = request.getParameters();
        JpaServiceInstance instance = jpaServiceInstanceRepository.findByServiceInstanceId(request.getServiceInstanceId());
        if(credentials == null){
            credentials = new HashMap<String, Object>();
        }
        credentials.put("host", instance.getDashboardUrl());
        credentials.put("password", password);
        credentials.put("port", port);
        ServiceInstanceBinding serviceInstanceBinding = new ServiceInstanceBinding(request.getBindingId(), request.getServiceInstanceId(), credentials, null, request.getAppGuid());
        return serviceInstanceBinding;
    }

    @Override
    public ServiceInstanceBinding deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) throws ServiceBrokerException {
        ServiceInstanceBinding serviceInstanceBinding = new ServiceInstanceBinding(request.getBindingId(), request.getInstance().getServiceInstanceId(), null, null, null);
        return serviceInstanceBinding;
    }
}
