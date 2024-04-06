package com.example.springgraygateway.config;

import org.springframework.context.annotation.Bean;

/**
 * XXXX
 *
 * @author yinyong
 * @version 1.0
 * @classname GrayRuleConfig
 * @date 2024/4/6 16:35
 */
public class GrayRuleConfig {
    @Bean
    public GrayRule grayRule() {
        return new GrayRule();
    }
}
