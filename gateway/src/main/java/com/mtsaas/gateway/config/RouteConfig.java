package com.mtsaas.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // SECURITY Service
                .route("security-service", r -> r
                        .path("/SECURITY/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Gateway", "API-Gateway")
                        )
                        .uri("lb://SECURITY"))

                // ADMIN Service
                .route("admin-service", r -> r
                        .path("/ADMIN/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Gateway", "API-Gateway")
                        )
                        .uri("lb://ADMIN"))

                // TENANT Service
                .route("tenant-service", r -> r
                        .path("/TENANT/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Gateway", "API-Gateway")
                        )
                        .uri("lb://TENANT"))

                // AUDIT Service
                .route("audit-service", r -> r
                        .path("/AUDIT/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Gateway", "API-Gateway")
                        )
                        .uri("lb://AUDIT"))

                // DATASOURCE Service
                .route("datasource-service", r -> r
                        .path("/DATASOURCE/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Gateway", "API-Gateway")
                        )
                        .uri("lb://DATASOURCE"))

                .build();
    }
}
