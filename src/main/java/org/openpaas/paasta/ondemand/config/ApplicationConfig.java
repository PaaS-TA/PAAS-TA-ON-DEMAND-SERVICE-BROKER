package org.openpaas.paasta.ondemand.config;


import org.openpaas.servicebroker.model.Catalog;
import org.openpaas.servicebroker.model.Plan;
import org.openpaas.servicebroker.model.ServiceDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class ApplicationConfig {

    @Bean
    public Catalog catalog() {

        return new Catalog(Arrays.asList(
                new ServiceDefinition(
                        "id",
                        "name",
                        "desc",
                        true, // bindable
                        false, // updatable
                        Arrays.asList(
                                new Plan("plan_id",
                                        "plan_name",
                                        "plan_desc",
                                        getPlanMetadata("plan_type"))),
                        Arrays.asList("plan_name"),
                        getServiceDefinitionMetadata(),
                        null,
                        null)));
    }

    private Map<String, Object> getServiceDefinitionMetadata() {
        Map<String, Object> sdMetadata = new HashMap<String, Object>();
        sdMetadata.put("displayName", "on-demand");
        sdMetadata.put("imageUrl", "");
        sdMetadata.put("longDescription", "Paas-TA On-Demand");
        sdMetadata.put("providerDisplayName", "PaaS-TA");
        sdMetadata.put("documentationUrl", "https://paas-ta.kr");
        sdMetadata.put("supportUrl", "https://paas-ta.kr");
        return sdMetadata;
    }


    private Map<String, Object> getPlanMetadata(String planType) {
        Map<String, Object> planMetadata = new HashMap<>();
        planMetadata.put("costs", getCosts(planType));
        planMetadata.put("bullets", getBullets(planType));

        return planMetadata;
    }

    private List<Map<String, Object>> getCosts(String planType) {
        Map<String, Object> costsMap = new HashMap<>();
        Map<String, Object> amount = new HashMap<>();

        switch (planType) {
            case "A":
                amount.put("usd", 0.0);
                costsMap.put("amount", amount);
                costsMap.put("unit", "MONTHLY");

                break;
            case "B":
                amount.put("usd", 0.0);
                costsMap.put("amount", amount);
                costsMap.put("unit", "MONTHLY");

                break;
            default:
                amount.put("usd", 0.0);
                costsMap.put("amount", amount);
                costsMap.put("unit", "MONTHLY");
                break;
        }

        return Collections.singletonList(costsMap);
    }

    /**
     * Plan의 Bullets 정보를 담은 객체를 반환
     * @param planType
     * @return List:String
     */
    private List<String> getBullets(String planType) {

        return Arrays.asList("dedicate",
                "dedicate");
    }
}
