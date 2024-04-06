package com.example.springgraygateway;

import com.example.springgraygateway.config.GrayRuleConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.springgraygateway.*", excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GrayRuleConfig.class)
})
//@EnableFeignClients(basePackages = "com.example.springgraygateway.demos.nacosdiscoveryconsumer")
@RibbonClients(value = {
    // 只对文章服务进行灰度发布
    @RibbonClient(value = "spring-gray-article", configuration = GrayRuleConfig.class)
})
public class SpringGrayGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringGrayGatewayApplication.class, args);
    }
}
