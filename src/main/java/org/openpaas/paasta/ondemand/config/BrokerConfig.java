package org.openpaas.paasta.ondemand.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;



@Configuration
@ComponentScan(basePackages = { "org.openpaas.paasta.ondemand"})
public class BrokerConfig {
}
