package com.smartrent.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 以编程方式声明网关路由，作为 application.yml 中 spring.cloud.gateway.routes 的兜底。
 * 不依赖 yml 属性绑定，确保路由在任何情况下都能注册生效。
 */
@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator smartRentRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("r-system", r -> r.path(
                                "/api/user/**", "/api/message/**", "/api/admin/**",
                                "/api/collection/**", "/api/preference/**", "/api/browse-history/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8081"))
                .route("r-house", r -> r.path(
                                "/api/house/**", "/api/appointment/**", "/api/contract/**",
                                "/api/bill/**", "/api/file/**", "/api/recommend/**", "/api/review/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8082"))
                .route("r-agent", r -> r.path("/api/agent/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8085"))
                .build();
    }
}
