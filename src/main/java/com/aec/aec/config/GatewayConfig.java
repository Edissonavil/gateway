package com.aec.aec.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
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
  public CorsWebFilter corsWebFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(
      "https://aecf-production.up.railway.app"
      
    ));
    config.setAllowCredentials(true);
    config.addAllowedMethod("*");
    config.addAllowedHeader("*");

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return new CorsWebFilter(source);
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