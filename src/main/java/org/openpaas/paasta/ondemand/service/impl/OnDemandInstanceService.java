package org.openpaas.paasta.ondemand.service.impl;


import org.openpaas.paasta.ondemand.config.ServiceBrokerVersion;
import org.openpaas.paasta.ondemand.exception.OndemandServiceException;
import org.openpaas.servicebroker.exception.ServiceBrokerException;
import org.openpaas.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.openpaas.servicebroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.openpaas.servicebroker.model.CreateServiceInstanceRequest;
import org.openpaas.servicebroker.model.DeleteServiceInstanceRequest;
import org.openpaas.servicebroker.model.UpdateServiceInstanceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openpaas.servicebroker.model.ServiceInstance;
import org.openpaas.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class OnDemandInstanceService implements ServiceInstanceService {

    private static final Logger logger = LoggerFactory.getLogger(OnDemandInstanceService.class);


    @Override
    public ServiceInstance createServiceInstance(CreateServiceInstanceRequest request) {
        return new ServiceInstance(request);
    }

    @Override
    public ServiceInstance  updateServiceInstance(UpdateServiceInstanceRequest request)  throws ServiceInstanceUpdateNotSupportedException, ServiceBrokerException, ServiceInstanceDoesNotExistException {
        throw new OndemandServiceException("Not Supported");
    }

    @Override
    public ServiceInstance deleteServiceInstance(DeleteServiceInstanceRequest request) {
        return new ServiceInstance(request);
    }

    @Override
    public ServiceInstance getServiceInstance(String Instanceid) {
        return null;
    }


}
