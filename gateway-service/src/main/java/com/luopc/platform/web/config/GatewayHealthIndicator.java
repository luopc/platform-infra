package com.luopc.platform.web.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 网关健康检查指示器
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class GatewayHealthIndicator implements HealthIndicator {

    private final RouteLocator routeLocator;

    @Override
    public Health health() {
        try {
            AtomicInteger routeCount = new AtomicInteger();

            // 统计路由数量
            routeLocator.getRoutes().subscribe(route -> {
                routeCount.incrementAndGet();
                log.debug("Found route: {} -> {}", route.getId(), route.getUri());
            });

            if (routeCount.get() > 0) {
                return Health.up()
                        .withDetail("routes", routeCount.get())
                        .withDetail("status", "Gateway is running with " + routeCount.get() + " routes")
                        .build();
            } else {
                return Health.down()
                        .withDetail("routes", 0)
                        .withDetail("status", "No routes configured")
                        .build();
            }

        } catch (Exception e) {
            log.error("Health check failed", e);
            return Health.down(e)
                    .withDetail("status", "Gateway health check failed")
                    .build();
        }
    }
}
