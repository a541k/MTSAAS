package com.mtsaas.gateway.filter;

import com.mtsaas.gateway.config.security.RouteValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private RouteValidator routeValidator;

    @Value("${jwt.secret}")
    private String secretKey;

    public JwtAuthenticationFilter(RouteValidator routeValidator) {
        this.routeValidator = routeValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Skip JWT validation for public endpoints
        if (!routeValidator.isSecured.test(request)) {
            log.info("Public endpoint accessed: {}", request.getURI().getPath());
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Check if Authorization header exists
        if (authHeader == null || authHeader.isEmpty()) {
            log.warn("Missing Authorization header for: {}", request.getURI().getPath());
            return onError(exchange.getResponse(), "Missing Authorization header", HttpStatus.UNAUTHORIZED);
        }

        // Check if header starts with Bearer
        if (!authHeader.startsWith("Bearer ")) {
            log.warn("Invalid Authorization header format");
            return onError(exchange.getResponse(), "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
        }

        // Extract token
        String token = authHeader.substring(7);

        try {
            // Validate token
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Extract user information from claims
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            log.info("JWT validated successfully for user: {}", username);

            // Add user information to request headers for downstream services
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Name", username)
                    .header("X-User-Role", role != null ? role : "")
                    .build();

            // Continue with modified request
            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            return onError(exchange.getResponse(), "JWT token expired", HttpStatus.UNAUTHORIZED);
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            return onError(exchange.getResponse(), "Invalid JWT signature", HttpStatus.UNAUTHORIZED);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            return onError(exchange.getResponse(), "Malformed JWT token", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
            return onError(exchange.getResponse(), "JWT validation error", HttpStatus.UNAUTHORIZED);
        }
    }

    private Mono<Void> onError(ServerHttpResponse response, String message, HttpStatus status) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = "{\"error\": \"" + message + "\", \"status\": " + status.value() + "}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1; // Run before other filters
    }
}
