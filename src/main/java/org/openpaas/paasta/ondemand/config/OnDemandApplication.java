package org.openpaas.paasta.ondemand.config;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@EnableAutoConfiguration
@ComponentScan
public class OnDemandApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnDemandApplication.class, args);
    }
}

