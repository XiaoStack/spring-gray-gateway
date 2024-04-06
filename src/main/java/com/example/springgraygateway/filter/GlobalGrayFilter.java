package com.example.springgraygateway.filter;

import com.example.springgraygateway.common.GrayConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
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
        // 1.解析请求头，查看是否存在灰度发布的请求头信息，如果存在则将其放置在ThreadLocal中
        HttpHeaders headers = exchange.getRequest().getHeaders();
        if (headers.containsKey(GrayConstant.GRAY_HEADER)) {
            String gray = headers.getFirst(GrayConstant.GRAY_HEADER);
            if (GrayConstant.GRAY_VALUE.equals(gray)) {
                // 2.设置灰度标记"Accept" -> {HeadersUtils$1@12573}  size = 1
                GrayRequestContextHolder.setGrayTag(true);
            }
            // 3.将灰度标记放入请求头中
            ServerHttpRequest tokenRequest = exchange.getRequest().mutate()
                // 将灰度标记传递过去
                .header(GrayConstant.GRAY_HEADER, GrayRequestContextHolder.getGrayTag().toString())
                .build();
            ServerWebExchange build = exchange.mutate().request(tokenRequest).build();
            return chain.filter(build);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
