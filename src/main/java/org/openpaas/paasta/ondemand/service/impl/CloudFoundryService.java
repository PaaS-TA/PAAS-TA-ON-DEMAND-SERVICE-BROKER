package org.openpaas.paasta.ondemand.service.impl;

import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.applications.RestageApplicationRequest;
import org.cloudfoundry.client.v2.securitygroups.*;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingsV2;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.openpaas.paasta.ondemand.common.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CloudFoundryService {

    @Value("${bosh.instance_name}")
    public String instance_name;

    @Autowired
    Common common;

    private static final Logger logger = LoggerFactory.getLogger(CloudFoundryService.class);

    public void ServiceInstanceAppBinding(String AppId, String ServiceInstanceId, Map parameters, ServiceBindingsV2 serviceBindingV2, ApplicationsV2 applicationsV2) throws Exception{
        serviceBindingV2.create(CreateServiceBindingRequest.builder().applicationId(AppId)
                        .serviceInstanceId(ServiceInstanceId).parameters(parameters).build()).block();
        applicationsV2.restage(RestageApplicationRequest.builder().applicationId(AppId).build()).block();
    }

    public void SecurityGurop(String space_id, String url, SecurityGroups securityGroups) throws Exception{
        /*
        시큐리티 그룹 조회
         */
        ListSecurityGroupsResponse  listSecurityGroupsResponse = securityGroups.list(ListSecurityGroupsRequest.builder().build()).block();
        List<SecurityGroupResource> resources = listSecurityGroupsResponse.getResources().stream().filter(result -> result.getEntity().getName().equals(instance_name + "_" + space_id)).collect(Collectors.toList());
        if(resources.isEmpty()){
            for(int i = 2 ; i <= listSecurityGroupsResponse.getTotalPages(); i++)
            {
                resources = securityGroups.list(ListSecurityGroupsRequest.builder().page(i).build()).block().getResources().stream().filter(result -> result.getEntity().getName().equals(instance_name + "_" + space_id)).collect(Collectors.toList());
                if(!resources.isEmpty()){
                    UpdateSecurityGroup(securityGroups, url, resources);
                   return;
                }
            }
        } else {
            UpdateSecurityGroup(securityGroups, url, resources);
            return;
        }
        securityGroups.create(CreateSecurityGroupRequest.builder()
                .name(instance_name + "_" + space_id)
                .rule(RuleEntity.builder()
                        .protocol(Protocol.ALL)
                        .destination(url)
                        .build())
                .spaceId(space_id)
                .build()).block();
    }

    private void UpdateSecurityGroup(SecurityGroups securityGroups, String url, List<SecurityGroupResource> resources){
        try {
            List<RuleEntity> ruleEntities = securityGroups.get(GetSecurityGroupRequest.builder().securityGroupId(resources.get(0).getMetadata().getId()).build()).block().getEntity().getRules();
            List<RuleEntity> rules = new ArrayList<>();
            rules.add(RuleEntity.builder()
                    .protocol(Protocol.ALL)
                    .destination(url)
                    .build());
            rules.addAll(ruleEntities);
            securityGroups.update(UpdateSecurityGroupRequest.builder().name(resources.get(0).getEntity().getName()).securityGroupId(resources.get(0).getMetadata().getId()).rules(rules).build()).block();
        } catch (Exception e){
            logger.info(e.getMessage());
        }
    }

    public void DelSecurityGurop(String space_id, String url){
        ReactorCloudFoundryClient reactorCloudFoundryClient = common.cloudFoundryClient();
        /*
        시큐리티 그룹 조회
         */
        ListSecurityGroupsResponse  listSecurityGroupsResponse = reactorCloudFoundryClient.securityGroups().list(ListSecurityGroupsRequest.builder().build()).block();
        List<SecurityGroupResource> resources = listSecurityGroupsResponse.getResources().stream().filter(result -> result.getEntity().getName().equals(instance_name + "_" + space_id)).collect(Collectors.toList());
        if(resources.isEmpty()){
            for(int i = 2 ; i <= listSecurityGroupsResponse.getTotalPages(); i++)
            {
                resources = reactorCloudFoundryClient.securityGroups().list(ListSecurityGroupsRequest.builder().page(i).build()).block().getResources().stream().filter(result -> result.getEntity().getName().equals(instance_name + "_" + space_id)).collect(Collectors.toList());
                if(!resources.isEmpty()){
                    DelUpdateSecurityGroup(reactorCloudFoundryClient, url, resources);
                    return;
                }
            }
        } else {
            DelUpdateSecurityGroup(reactorCloudFoundryClient, url, resources);
            return;
        }
    }

    private void DelUpdateSecurityGroup(ReactorCloudFoundryClient reactorCloudFoundryClient, String url, List<SecurityGroupResource> resources){
        try {
            List<RuleEntity> ruleEntities = reactorCloudFoundryClient.securityGroups().get(GetSecurityGroupRequest.builder().securityGroupId(resources.get(0).getMetadata().getId()).build()).block().getEntity().getRules();
            List<RuleEntity> rules = new ArrayList<>();
            rules.addAll(ruleEntities);
            if(rules.size() <= 1){
                reactorCloudFoundryClient.securityGroups().delete(DeleteSecurityGroupRequest.builder().securityGroupId(resources.get(0).getMetadata().getId()).async(true).build());
            }else{
                for(RuleEntity rule : rules) {
                    if (rule.getDestination().equals(url)) {
                        rules.remove(rule);
                        return;
                    }
                }
                reactorCloudFoundryClient.securityGroups().update(UpdateSecurityGroupRequest.builder().name(resources.get(0).getEntity().getName()).securityGroupId(resources.get(0).getMetadata().getId()).rules(rules).build()).block();
            }
        } catch (Exception e){
            logger.info(e.getMessage());
        }
    }

}
