package com.mtsaas.gateway.config.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    // Public endpoints - NO JWT required
    public static final List<String> openEndpoints = List.of(
            "/api/auth/user/login",
            "/api/auth/user/register",
            "/swagger-ui",
            "/v3/api-docs",
            "/actuator"
    );

    public Predicate<ServerHttpRequest> isSecured = request -> {
        String path = request.getURI().getPath();
        
        // Remove service prefix (e.g., /SECURITY/api/... -> /api/...)
        if (path.startsWith("/")) {
            int secondSlash = path.indexOf("/", 1);
            if (secondSlash > 0) {
                path = path.substring(secondSlash);
            }
        }
        
        String finalPath = path;
        return openEndpoints.stream()
                .noneMatch(endpoint -> finalPath.startsWith(endpoint));
    };
}
