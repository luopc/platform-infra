package com.luopc.platform.web.config;


import com.alibaba.nacos.client.naming.event.InstancesChangeEvent;
import com.alibaba.nacos.common.notify.Event;
import com.alibaba.nacos.common.notify.NotifyCenter;
import com.alibaba.nacos.common.notify.listener.Subscriber;
import com.github.xiaoymin.knife4j.spring.gateway.Knife4jGatewayProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springdoc.core.utils.Constants.DEFAULT_API_DOCS_URL;
import static org.springframework.cloud.loadbalancer.core.CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME;

/**
 * 监听注册中心实例注册状态改变事件，微服务实例状态改变后刷新swagger
 *
 * @author vains
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class InstancesChangeEventListener extends Subscriber<InstancesChangeEvent> {

    private static final String LB_SCHEME = "lb";
    private static final String PATH_SEPARATOR = "/";

    private final RouteDefinitionLocator locator;

    @Resource
    private CacheManager defaultLoadBalancerCacheManager;
    @Resource
    private Knife4jGatewayProperties knife4jGatewayProperties;

    public InstancesChangeEventListener(RouteDefinitionLocator locator) {
        this.locator = locator;
    }

    @Override
    public void onEvent(InstancesChangeEvent event) {
        log.info("--------------------------------- Received change event to refresh instances ---------------------------------");
        try {
            Cache cache = defaultLoadBalancerCacheManager.getCache(SERVICE_INSTANCE_CACHE_NAME);
            if (cache != null) {
                cache.evict(event.getServiceName());
            }
            this.refreshGroup();
            log.info("--------------------------------- Finished refreshing route definitions ---------------------------------");
        } catch (Exception e) {
            log.error("处理实例刷新事件时发生异常：{}", event.getServiceName(), e);
        }
    }


    /**
     * 刷新 swagger 的 group
     */
    public void refreshGroup() {
        if (knife4jGatewayProperties == null) {
            log.warn("Knife4jGatewayProperties 未初始化，跳过刷新");
            return;
        }
        List<RouteDefinition> definitions = locator.getRouteDefinitions()
                .collectList()
                .block();
        if (CollectionUtils.isEmpty(definitions)) {
            log.debug("未找到任何路由定义");
            return;
        }
        Set<String> excludeServices = knife4jGatewayProperties.getDiscover().getExcludedServices();
        List<Knife4jGatewayProperties.Router> routers = convertToRouters(definitions, excludeServices);
        updateKnife4jRoutes(routers);
    }

    /**
     * 将 RouteDefinition 列表转换为 Router 列表
     */
    private List<Knife4jGatewayProperties.Router> convertToRouters(
            List<RouteDefinition> definitions,
            Set<String> excludeServices) {

        List<Knife4jGatewayProperties.Router> routers = new ArrayList<>(definitions.size());

        for (RouteDefinition definition : definitions) {
            try {
                log.info("Process service: {}" , definition.getId());
                if (!isValidRoute(definition, excludeServices)) {
                    continue;
                }

                String serviceName = definition.getUri().getAuthority();
                Knife4jGatewayProperties.Router router = convertToRouter(serviceName, definition);
                if (router != null) {
                    routers.add(router);
                }
            } catch (Exception e) {
                log.error("转换路由失败：{}", definition.getId(), e);
            }
        }
        return routers;
    }

    /**
     * 验证路由是否有效
     */
    private boolean isValidRoute(RouteDefinition definition, Set<String> excludeServices) {
        if (definition.getUri() == null) {
            log.info("URI is null, skipping: {}", definition.getId());
            return false;
        }

        if (!LB_SCHEME.equals(definition.getUri().getScheme())) {
            log.info("Skip non-lb route: {} with uri: {}", definition.getId(), definition.getUri());
            return false;
        }

        String serviceName = definition.getUri().getAuthority();
        if (excludeServices.contains(serviceName)) {
            log.info("Skip excluded service: {}", serviceName);
            return false;
        }

        return true;
    }

    /**
     * 更新 Knife4j 路由配置
     */
    private void updateKnife4jRoutes(List<Knife4jGatewayProperties.Router> routers) {
        if (CollectionUtils.isEmpty(routers)) {
            log.debug("no routers need to be updated");
            return;
        }

        synchronized (knife4jGatewayProperties.getRoutes()) {
            knife4jGatewayProperties.getRoutes().clear();
            knife4jGatewayProperties.getRoutes().addAll(routers);
        }

        log.info("Updated knife4j gateway routes, count: {}", routers.size());
    }

    /**
     * 将 RouteDefinition 转换为 Knife4j Router
     *
     * @param serviceName 服务名称
     * @param definition  Spring Gateway 的路由定义
     * @return Knife4j 的 Router 配置
     */
    private Knife4jGatewayProperties.Router convertToRouter(String serviceName, RouteDefinition definition) {
        try {
            Knife4jGatewayProperties.Router router = new Knife4jGatewayProperties.Router();

            router.setServiceName(serviceName);

            Map<String, Knife4jGatewayProperties.ServiceConfigInfo> serviceConfigMap =
                    knife4jGatewayProperties.getDiscover().getServiceConfig();

            Knife4jGatewayProperties.ServiceConfigInfo serviceConfig =
                    serviceConfigMap != null ? serviceConfigMap.get(serviceName) : null;

            if (serviceConfig != null) {
                applyServiceConfig(router, serviceName, serviceConfig, definition);
            } else {
                applyDefaultConfig(router, serviceName, definition);
            }

            String contextPath = buildContextPath(serviceName, router.getContextPath());
            router.setContextPath(contextPath);
            router.setUrl(buildApiDocsUrl(contextPath));

            log.info("Converted route: {} -> Router{name={}, serviceName={}, contextPath={}, url={}, order={}}",
                    definition.getId(), router.getName(), router.getServiceName(),
                    router.getContextPath(), router.getUrl(), router.getOrder());

            return router;
        } catch (Exception e) {
            log.error("Failed to convert RouteDefinition to Router: {}", definition.getId(), e);
            return null;
        }
    }

    /**
     * 应用服务配置
     */
    private void applyServiceConfig(Knife4jGatewayProperties.Router router,
                                    String serviceName,
                                    Knife4jGatewayProperties.ServiceConfigInfo serviceConfig,
                                    RouteDefinition definition) {

        log.info("Service Order, {} : {}", serviceName, serviceConfig.getOrder());
        log.info("Service ContextPath, {} : {}", serviceName, serviceConfig.getContextPath());

        router.setName(StringUtils.hasText(serviceConfig.getGroupName()) ?
                serviceConfig.getGroupName() : serviceName);

        Integer order = serviceConfig.getOrder();
        router.setOrder(order != null ? order : definition.getOrder());

        router.setContextPath(serviceConfig.getContextPath());
    }

    /**
     * 应用默认配置
     */
    private void applyDefaultConfig(Knife4jGatewayProperties.Router router,
                                    String serviceName,
                                    RouteDefinition definition) {
        router.setName(serviceName);
        router.setOrder(definition.getOrder());
    }

    /**
     * 构建完整的 ContextPath
     *
     * @param serviceName 服务名称
     * @param extraPath   额外的路径前缀
     * @return 完整的上下文路径
     */
    private String buildContextPath(String serviceName, String extraPath) {
        StringBuilder pathBuilder = new StringBuilder(PATH_SEPARATOR)
                .append(serviceName);

        if (StringUtils.hasText(extraPath)) {
            String normalizedPath = normalizePath(extraPath);
            if (!PATH_SEPARATOR.equals(normalizedPath)) {
                pathBuilder.append(normalizedPath);
            }
        }

        return pathBuilder.toString();
    }

    /**
     * 标准化路径，确保以/开头
     */
    private String normalizePath(String path) {
        if (!StringUtils.hasText(path)) {
            return PATH_SEPARATOR;
        }
        return path.startsWith(PATH_SEPARATOR) ? path : PATH_SEPARATOR + path;
    }

    /**
     * 构建 API 文档的完整 URL
     *
     * @param contextPath 上下文路径
     * @return API 文档 URL
     */
    private String buildApiDocsUrl(String contextPath) {
        StringBuilder urlBuilder = new StringBuilder();

        if (StringUtils.hasText(contextPath)) {
            urlBuilder.append(normalizePath(contextPath));
        }

        urlBuilder.append(DEFAULT_API_DOCS_URL)
                .append("?group=default");

        return urlBuilder.toString();
    }

    @PostConstruct
    public void registerToNotifyCenter() {
        NotifyCenter.registerSubscriber(this);
    }

    @Override
    public Class<? extends Event> subscribeType() {
        return InstancesChangeEvent.class;
    }
}
