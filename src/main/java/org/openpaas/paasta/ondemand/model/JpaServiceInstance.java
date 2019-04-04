package org.openpaas.paasta.ondemand.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openpaas.servicebroker.model.*;

import javax.persistence.*;


@Entity
@Table(name = "on_demand_info")
public class JpaServiceInstance extends ServiceInstance {

    @JsonSerialize
    @JsonProperty("service_instance_id")
    @Id
    @Column(name = "service_instance_id")
    private String serviceInstanceId;

    @JsonSerialize
    @Column(name = "service_id")
    @JsonProperty("service_id")
    private String serviceDefinitionId;

    @JsonSerialize
    @Column(name = "plan_id")
    @JsonProperty("plan_id")
    private String planId;

    @JsonSerialize
    @Column(name = "organization_guid")
    @JsonProperty("organization_guid")
    private String organizationGuid;

    @JsonSerialize
    @Column(name = "space_guid")
    @JsonProperty("space_guid")
    private String spaceGuid;

    @JsonSerialize
    @Column(name = "dashboard_url")
    @JsonProperty("dashboard_url")
    private String dashboardUrl;

    @Transient
    @JsonIgnore
    private boolean async;

    @JsonSerialize
    @JsonProperty("vm_instance_id")
    @Column(name = "vm_instance_id")
    private String vmInstanceId;

    @JsonSerialize
    @JsonProperty("use_yn")
    @Column(name = "use_yn")
    private String useYn;

    @JsonSerialize
    @JsonProperty("task_id")
    @Column(name = "task_id")
    private String taskId;


    public JpaServiceInstance(){
        super();
    }

    public JpaServiceInstance(CreateServiceInstanceRequest request) {
        super(request);
        setServiceDefinitionId(request.getServiceDefinitionId());
        setPlanId(request.getPlanId());
        setOrganizationGuid(request.getOrganizationGuid());
        setSpaceGuid(request.getSpaceGuid());
        setServiceInstanceId(request.getServiceInstanceId());
    }

    public JpaServiceInstance(DeleteServiceInstanceRequest request){
        super(request);

        setServiceDefinitionId(request.getServiceId());
        setServiceInstanceId(request.getServiceInstanceId());
        setPlanId(request.getPlanId());
    }

    public JpaServiceInstance(UpdateServiceInstanceRequest request) {
        // service (definition) id, service instance id, plan id
        super(request);

        setServiceDefinitionId(request.getServiceDefinitionId());
        setServiceInstanceId(request.getServiceInstanceId());
        setPlanId(request.getPlanId());
    }


    @Override
    public String getDashboardUrl() {
        return dashboardUrl;
    }

    public void setDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
    }

    public String getUseYn() {
        return useYn;
    }

    public void setUseYn(String useYn) {
        this.useYn = useYn;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    @Override
    public String getServiceDefinitionId() {
        return serviceDefinitionId;
    }

    public void setServiceDefinitionId(String serviceDefinitionId) {
        this.serviceDefinitionId = serviceDefinitionId;
    }

    @Override
    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    @Override
    public String getSpaceGuid() {
        return spaceGuid;
    }

    public void setSpaceGuid(String spaceGuid) {
        this.spaceGuid = spaceGuid;
    }

    @Override
    public String getOrganizationGuid() {
        return organizationGuid;
    }

    public void setOrganizationGuid(String organizationGuid) {
        this.organizationGuid = organizationGuid;
    }

    public String getVmInstanceId() {
        return vmInstanceId;
    }

    public void setVmInstanceId(String vmInstanceId) {
        this.vmInstanceId = vmInstanceId;
    }

    @Override
    public boolean isAsync() {
        return async;
    }

    @Override
    public ServiceInstance and() {
        return this;
    }

    @Override
    public ServiceInstance withDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
        return this;
    }

    @Override
    public ServiceInstance withAsync(boolean async) {
        this.async = async;
        return this;
    }

    @Override
    public String toString() {
        return "JpaServiceInstance{" +
                "serviceInstanceId='" + serviceInstanceId + '\'' +
                ", serviceDefinitionId='" + serviceDefinitionId + '\'' +
                ", planId='" + planId + '\'' +
                ", organizationGuid='" + organizationGuid + '\'' +
                ", spaceGuid='" + spaceGuid + '\'' +
                ", dashboardUrl='" + dashboardUrl + '\'' +
                ", async=" + async +
                ", vmInstanceId='" + vmInstanceId + '\'' +
                ", useYn='" + useYn + '\'' +
                ", taskId='" + taskId + '\'' +
                '}';
    }
}