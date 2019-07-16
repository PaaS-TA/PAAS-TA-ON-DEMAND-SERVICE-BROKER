package model;

import org.openpaas.paasta.ondemand.model.JpaServiceInstance;
import org.openpaas.servicebroker.model.ServiceInstance;

public class ServiceInstanceModel {

    public static ServiceInstance getServiceInstance() {

        return new ServiceInstance(
                ServiceInstanceRequestModel.getCreateServiceInstanceRequest()
                        .withServiceInstanceId("ServiceInstance_id"))
                .withDashboardUrl("DashBoard Url");

    }

    public static JpaServiceInstance getJpaServiceInstance(){
        return new JpaServiceInstance(ServiceInstanceRequestModel.getCreateServiceInstanceRequest()
                .withServiceInstanceId("ServiceInstance_id"))
                .withDashboardUrl("dashboardUrl");
    }

}
