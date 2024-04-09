package com.example.springgraygateway.config;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.example.springgraygateway.common.GrayConstant;
import com.example.springgraygateway.common.GrayRequestContextHolder;
import com.google.common.base.Optional;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 灰度服务负载均衡策略
 *
 * @author yinyong
 * @version 1.0
 * @classname GrayRule
 * @date 2024/4/6 16:23
 */
@Slf4j
public class GrayRule extends ZoneAvoidanceRule {

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
    }

    @Override
    public Server choose(Object key) {
        try {
            // 从ThreadLocal中获取灰度标记
            boolean grayTag = GrayRequestContextHolder.getGrayTag();
            // 获取所有可用服务
            List<Server> serverList = this.getLoadBalancer().getReachableServers();
            // 灰度发布的服务
            List<Server> grayServerList = new ArrayList<>();
            // 正常的服务
            List<Server> normalServerList = new ArrayList<>();
            for (Server server : serverList) {
                NacosServer nacosServer = (NacosServer)server;
                // 从nacos中获取元素剧进行匹配
                if (nacosServer.getMetadata().containsKey(GrayConstant.GRAY_HEADER)
                    && nacosServer.getMetadata().get(GrayConstant.GRAY_HEADER).equals(GrayConstant.GRAY_VALUE)) {
                    grayServerList.add(server);
                } else {
                    normalServerList.add(server);
                }
            }
            // 如果被标记为灰度发布，则调用灰度发布的服务
            if (grayTag) {
                return originChoose(grayServerList, key);
            } else {
                return originChoose(normalServerList, key);
            }
        } finally {
            // 清除灰度标记
            GrayRequestContextHolder.clear();
        }
    }

    private Server originChoose(List<Server> serverList, Object key) {
        Optional<Server> serverOptional =
            getPredicate().chooseRoundRobinAfterFiltering(serverList, key);
        if (serverOptional.isPresent()) {
            return serverOptional.get();
        } else {
            return null;
        }
    }
}
