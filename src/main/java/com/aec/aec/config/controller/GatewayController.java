package com.aec.aec.config.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/gateway")
@CrossOrigin(origins = "*")
public class GatewayController {

    private final RouteLocator routeLocator;

    @Value("${spring.application.name:api-gateway}")
    private String applicationName;

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    public GatewayController(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", applicationName);
        health.put("profile", activeProfile);
        health.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(health);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", applicationName);
        info.put("version", "1.0.0");
        info.put("profile", activeProfile);
        info.put("description", "API Gateway for Microservices Architecture");
        info.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(info);
    }

    @GetMapping("/routes")
    public Mono<ResponseEntity<Flux<Route>>> routes() {
        return Mono.just(ResponseEntity.ok(routeLocator.getRoutes()));
    }

    @GetMapping("/services")
    public ResponseEntity<Map<String, String>> services() {
        Map<String, String> services = new HashMap<>();
        
        if ("railway".equals(activeProfile)) {
            services.put("users-service", "https://users-service.railway.internal");
            services.put("auth-service", "https://auth-service.railway.internal");
            services.put("prod-service", "https://prod-service.railway.internal");
            services.put("file-service", "https://file-service.railway.internal");
            services.put("order-service", "https://order-service-deploy.railway.internal");
            services.put("stats-service", "https://stats-service.railway.internal");
        } else {
            services.put("users-service", "http://localhost:8081");
            services.put("auth-service", "http://localhost:8082");
            services.put("prod-service", "http://localhost:8083");
            services.put("file-service", "http://localhost:8084");
            services.put("order-service", "http://localhost:8085");
            services.put("stats-service", "http://localhost:8086");
        }
        
        return ResponseEntity.ok(services);
    }

    @PostMapping("/test/{service}")
    public ResponseEntity<Map<String, Object>> testService(@PathVariable String service) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Test endpoint for " + service + " service");
        response.put("service", service);
        response.put("gateway", applicationName);
        response.put("profile", activeProfile);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}