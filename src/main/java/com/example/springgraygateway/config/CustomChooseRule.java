//package com.example.springgraygateway.config;
//
//import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
//import com.alibaba.cloud.nacos.ribbon.NacosServer;
//import com.alibaba.nacos.api.exception.NacosException;
//import com.alibaba.nacos.api.naming.NamingService;
//import com.alibaba.nacos.api.naming.pojo.Instance;
//import com.netflix.client.config.IClientConfig;
//import com.netflix.loadbalancer.AbstractLoadBalancerRule;
//import com.netflix.loadbalancer.BaseLoadBalancer;
//import com.netflix.loadbalancer.Server;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.StringUtils;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * XXXX
// *
// * @author yinyong
// * @version 1.0
// * @classname GrayRule
// * @date 2024/4/6 15:32
// */
//@Slf4j
//public class CustomChooseRule extends AbstractLoadBalancerRule {
//
//    @Resource
//    private NacosDiscoveryProperties discoveryProperties;
//
//    @Override
//    public void initWithNiwsConfig(IClientConfig clientConfig) {
//
//    }
//
//    @Override
//    public Server choose(Object key) {
//        try {
//            // 获取本地所部署集群的名称
//            String localClusterName = discoveryProperties.getClusterName();
//
//            // 去 nacos 上获取和本地相同集群 相同版本的所有实例信息
//            List<Instance> theSameClusterNameAndTheSameVersionInstList =
//                getTheSameClusterAndTheSameVersionInstances(discoveryProperties);
//
//            // 声明被调用的实例
//            Instance toBeChooseInstance;
//
//            // 判断同集群同版本号的微服务实例是否为空
//            if (theSameClusterNameAndTheSameVersionInstList.isEmpty()) {
//                // 跨集群调用相同的版本
//                toBeChooseInstance = crossClusterAndTheSameVersionInovke(discoveryProperties);
//            } else {
//                // 具有同集群  同版本号的实例
//                toBeChooseInstance =
//                    ArtisanWeightedBalancer.chooseInstanceByRandomWeight(theSameClusterNameAndTheSameVersionInstList);
//                log.info("同集群同版本调用--->当前微服务所在集群:{},被调用微服务所在集群:{},当前微服务的版本:{},被调用微服务版本:{},Host:{},Port:{}",
//                    localClusterName,
//                    toBeChooseInstance.getClusterName(),
//                    discoveryProperties.getMetadata().get("current-version"),
//                    toBeChooseInstance.getMetadata().get("current-version"),
//                    toBeChooseInstance.getIp(),
//                    toBeChooseInstance.getPort());
//            }
//            return new NacosServer(toBeChooseInstance);
//        } catch (NacosException e) {
//            log.error("同集群优先权重负载均衡算法选择异常", e);
//            return null;
//        }
//    }
//
//    /**
//     * 方法实现说明:获取相同集群下,相同版本的 所有实例
//     *
//     * @param discoveryProperties nacos的配置
//     * @author:smlz
//     * @return: List
//     * @exception: NacosException
//     */
//    private List<Instance> getTheSameClusterAndTheSameVersionInstances(NacosDiscoveryProperties discoveryProperties)
//        throws NacosException {
//
//        //当前的集群的名称
//        String currentClusterName = discoveryProperties.getClusterName();
//
//        //当前的版本号
//        String currentVersion = discoveryProperties.getMetadata().get("current-version");
//
//        //获取所有实例的信息(包括不同集群的,不同版本号的)
//        List<Instance> allInstance = getAllInstances(discoveryProperties);
//
//        List<Instance> theSameClusterNameAndTheSameVersionInstList = new ArrayList<>();
//
//        //过滤相同集群  同版本号的实例
//        for (Instance instance : allInstance) {
//            if (StringUtils.endsWithIgnoreCase(instance.getClusterName(), currentClusterName) &&
//                StringUtils.endsWithIgnoreCase(instance.getMetadata().get("current-version"), currentVersion)) {
//
//                theSameClusterNameAndTheSameVersionInstList.add(instance);
//            }
//        }
//
//        return theSameClusterNameAndTheSameVersionInstList;
//    }
//
//    /**
//     * 方法实现说明:获取被调用服务的所有实例
//     *
//     * @param discoveryProperties nacos的配置
//     * @author:smlz
//     * @return: List
//     * @exception: NacosException
//     */
//    private List<Instance> getAllInstances(NacosDiscoveryProperties discoveryProperties)
//        throws NacosException {
//        // 第1步:获取一个负载均衡对象
//        BaseLoadBalancer baseLoadBalancer = (BaseLoadBalancer)getLoadBalancer();
//
//        // 第2步:获取当前调用的微服务的名称
//        String invokedSerivceName = baseLoadBalancer.getName();
//
//        // 第3步:获取nacos clinet的服务注册发现组件的api
//        NamingService namingService = discoveryProperties.namingServiceInstance();
//
//        // 第4步:获取所有的服务实例
//        return namingService.getAllInstances(invokedSerivceName);
//    }
//
//    /**
//     * 方法实现说明:跨集群环境下 相同版本的
//     *
//     * @param discoveryProperties
//     * @author:smlz
//     * @return: List
//     * @exception: NacosException
//     */
//    private List<Instance> getCrossClusterAndTheSameVersionInstList(NacosDiscoveryProperties discoveryProperties)
//        throws NacosException {
//        // 版本号
//        String currentVersion = discoveryProperties.getMetadata().get("current-version");
//
//        // 被调用的所有实例
//        List<Instance> allInstance = getAllInstances(discoveryProperties);
//        List<Instance> crossClusterAndTheSameVersionInstList = new ArrayList<>();
//
//        // 过滤相同版本
//        for (Instance instance : allInstance) {
//            if (StringUtils.endsWithIgnoreCase(instance.getMetadata().get("current-version"), currentVersion)) {
//                crossClusterAndTheSameVersionInstList.add(instance);
//            }
//        }
//
//        return crossClusterAndTheSameVersionInstList;
//    }
//
//    private Instance crossClusterAndTheSameVersionInovke(NacosDiscoveryProperties discoveryProperties)
//        throws NacosException {
//
//        //获取所有集群下相同版本的实例信息
//        List<Instance> crossClusterAndTheSameVersionInstList =
//            getCrossClusterAndTheSameVersionInstList(discoveryProperties);
//        //当前微服务的版本号
//        String currentVersion = discoveryProperties.getMetadata().get("current-version");
//        //当前微服务的集群名称
//        String currentClusterName = discoveryProperties.getClusterName();
//
//        //声明被调用的实例
//        Instance toBeChooseInstance = null;
//
//        //没有对应相同版本的实例
//        if (crossClusterAndTheSameVersionInstList.isEmpty()) {
//            log.info("跨集群调用找不到对应合适的版本当前版本为:currentVersion:{}", currentVersion);
//            throw new RuntimeException("找不到相同版本的微服务实例");
//        } else {
//            toBeChooseInstance =
//                ArtisanWeightedBalancer.chooseInstanceByRandomWeight(crossClusterAndTheSameVersionInstList);
//
//            log.info("跨集群同版本调用--->当前微服务所在集群:{},被调用微服务所在集群:{},当前微服务的版本:{},被调用微服务版本:{},Host:{},Port:{}",
//                currentClusterName,
//                toBeChooseInstance.getClusterName(),
//                discoveryProperties.getMetadata().get("current-version"),
//                toBeChooseInstance.getMetadata().get("current-version"),
//                toBeChooseInstance.getIp(),
//                toBeChooseInstance.getPort());
//        }
//
//        return toBeChooseInstance;
//    }
//}
