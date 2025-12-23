package com.mtsaas.auth_service.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTService {

    @Value("${jwt.secret}")
    private String secrectkeyString;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private SecretKey secretKey;

    @PostConstruct
    private void init(){
        this.secretKey = Keys.hmacShaKeyFor(secrectkeyString.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates token for user with default role
     */
    public String generateToken(UserDetails userDetails) {
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_CUSTOMER");

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return createToken(userDetails.getUsername(), claims);
    }

    /**
     * Creates signed JWT with claims, subject, and expiration
     */
    private String createToken(String username, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }

    public Boolean isTokenExpired(String token){
        return extractClaims(token).getExpiration().before(new Date());
    }

    public Boolean validateToken(String userName, String token){
        String extractedUserName = extractUsername(token);
        return extractedUserName.equals(userName) && !isTokenExpired(token);
    }

    public Instant tokenExpiryInstant() {
        return Instant.now().plusMillis(expirationTime);
    }
}
