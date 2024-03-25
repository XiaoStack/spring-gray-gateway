package com.example.springgraygateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 自定义全局过滤器
 *
 * @author yinyong
 * @version 1.0
 * @classname GlobalGrayFilter
 * @date 2024/3/20 0:22
 */
@Component
@Slf4j
public class GlobalGrayFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("---------执行全局过滤器-----------");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
