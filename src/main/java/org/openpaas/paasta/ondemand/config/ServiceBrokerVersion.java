package org.openpaas.paasta.ondemand.config;

import org.openpaas.servicebroker.model.BrokerApiVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceBrokerVersion {
    @Bean
    public BrokerApiVersion brokerApiVersion() {
        return new BrokerApiVersion("2.12");
    }
}
