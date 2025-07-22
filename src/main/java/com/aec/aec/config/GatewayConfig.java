package com.aec.aec.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Bean
    public GlobalFilter customGlobalFilter() {
        return (exchange, chain) -> {
            // Log incoming requests
            String path = exchange.getRequest().getPath().toString();
            String method = exchange.getRequest().getMethod().toString();
            
            System.out.println("Gateway routing request: " + method + " " + path);
            System.out.println("Active profile: " + activeProfile);
            
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // Log response status
                int statusCode = exchange.getResponse().getStatusCode().value();
                System.out.println("Response status: " + statusCode + " for " + path);
            }));
        };
    }

    @Bean
    public GlobalFilter corsHeadersFilter() {
        return (exchange, chain) -> {
            exchange.getResponse().getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            exchange.getResponse().getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE,OPTIONS,PATCH");
            exchange.getResponse().getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
            exchange.getResponse().getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            return chain.filter(exchange);
        };
    }

    @Bean
    public GlobalFilter requestLoggingFilter() {
        return (exchange, chain) -> {
            String requestId = java.util.UUID.randomUUID().toString().substring(0, 8);
            exchange.getAttributes().put("requestId", requestId);
            
            return chain.filter(exchange);
        };
    }
}