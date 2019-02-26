package org.openpaas.paasta.ondemand;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
public class OnDemandApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnDemandApplication.class, args);
    }
}

