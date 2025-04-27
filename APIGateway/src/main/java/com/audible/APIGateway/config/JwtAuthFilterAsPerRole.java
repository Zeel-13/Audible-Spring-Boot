package com.audible.APIGateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Component
public class JwtAuthFilterAsPerRole implements GatewayFilter {

    private static final String SECRET_KEY = "dd08bd1d7f4a3155d5c8cdb0c644c60df71c95862c15b2bfc7e2b396d63f078c";

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    private String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private String extractRole(String token) {
        Claims claim = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claim.get("role", String.class); // assumes "role" is a string like "ADMIN"
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("JWT filter triggered for path: " + exchange.getRequest().getPath());

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        try {
            String username = extractUsername(token);
            String role = extractRole(token);

            System.out.println("Username: " + username);
            System.out.println("Role: " + role);

            String path = exchange.getRequest().getPath().toString();

            // ✅ Check if the path is valid for the role
            if (path.startsWith("/admin/") && !"ADMIN".equalsIgnoreCase(role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            } else if (path.startsWith("/user/") && !"USER".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
                // If you want admins to also access /user/**, keep the second condition
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // 👇 Mutate request and add custom headers
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-Username", username)
                    .header("X-Role", role)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}

