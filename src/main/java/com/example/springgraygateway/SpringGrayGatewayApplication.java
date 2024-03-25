package com.example.springgraygateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages = "com.example.springgraygateway.*.*")
//@EnableFeignClients(basePackages = "com.example.springgraygateway.demos.nacosdiscoveryconsumer")
public class SpringGrayGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringGrayGatewayApplication.class, args);
    }

}
