package org.openpaas.paasta.ondemand.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@EnableJpaRepositories("org.openpaas.paasta.ondemand.repo")
@EntityScan(value = "org.openpaas.paasta.ondemand.model")
@ComponentScan(basePackages = { "org.openpaas.paasta.ondemand","org.openpaas.servicebroker"})
public class BrokerConfig {
}
