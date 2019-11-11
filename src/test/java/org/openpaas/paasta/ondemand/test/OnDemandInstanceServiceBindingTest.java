package org.openpaas.paasta.ondemand.test;

import model.ServiceInstanceRequestModel;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openpaas.paasta.ondemand.model.JpaServiceInstance;
import org.openpaas.paasta.ondemand.repo.JpaServiceInstanceRepository;
import org.openpaas.paasta.ondemand.service.impl.OnDemandInstanceServiceBinding;
import org.openpaas.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.openpaas.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.openpaas.servicebroker.model.ServiceInstanceBinding;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OnDemandInstanceServiceBindingTest {

    @InjectMocks
    OnDemandInstanceServiceBinding onDemandInstanceServiceBinding;

    @Mock
    JpaServiceInstanceRepository jpaServiceInstanceRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(onDemandInstanceServiceBinding, "password", "password");
        ReflectionTestUtils.setField(onDemandInstanceServiceBinding, "port", 1234);
    }

//    @Test
//    public void createServiceInstanceBinding_test1() throws Exception {
//        CreateServiceInstanceBindingRequest request = ServiceInstanceRequestModel.getCreateServiceInstanceBindingRequest();
//        JpaServiceInstance jpaServiceInstance = new JpaServiceInstance();
//        request.setParameters(null);
//        when(jpaServiceInstanceRepository.findByServiceInstanceId(anyString())).thenReturn(jpaServiceInstance);
//        ServiceInstanceBinding serviceInstanceBinding = onDemandInstanceServiceBinding.createServiceInstanceBinding(request);
//        assertThat(serviceInstanceBinding.getAppGuid(), is("app_guid"));
//        assertThat(serviceInstanceBinding.getCredentials().get("password"), is("password"));
//        assertThat(serviceInstanceBinding.getCredentials().get("port"), is(1234));
//    }
//
//    @Test
//    public void createServiceInstanceBinding_test2() throws Exception {
//        CreateServiceInstanceBindingRequest request = ServiceInstanceRequestModel.getCreateServiceInstanceBindingRequest();
//        JpaServiceInstance jpaServiceInstance = new JpaServiceInstance();
//        Map<String, Object> credentials = new HashMap<String, Object>();
//        credentials.put("test", "test");
//        request.setParameters(credentials);
//        when(jpaServiceInstanceRepository.findByServiceInstanceId(anyString())).thenReturn(jpaServiceInstance);
//        ServiceInstanceBinding serviceInstanceBinding = onDemandInstanceServiceBinding.createServiceInstanceBinding(request);
//        assertThat(serviceInstanceBinding.getAppGuid(), is("app_guid"));
//        assertThat(serviceInstanceBinding.getCredentials().get("password"), is("password"));
//        assertThat(serviceInstanceBinding.getCredentials().get("port"), is(1234));
//        assertThat(serviceInstanceBinding.getCredentials().get("test"), is("test"));
//    }
//
//    @Test
//    public void createServiceInstanceUnBinding_test1() throws Exception {
//        DeleteServiceInstanceBindingRequest request = ServiceInstanceRequestModel.getDeleteServiceInstanceBindingRequest();;
//        ServiceInstanceBinding serviceInstanceBinding = onDemandInstanceServiceBinding.deleteServiceInstanceBinding(request);
//        assertThat(serviceInstanceBinding.getServiceInstanceId(), is("ServiceInstance_id"));
//    }
}
