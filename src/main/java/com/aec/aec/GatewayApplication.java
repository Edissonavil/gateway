package com.aec.aec;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
    @Profile("local")           
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				// Users Service Routes
				.route("users-service", r -> r.path("/api/users/**")
						.uri("lb://users-service"))
				
				// Auth Service Routes
				.route("auth-service", r -> r.path("/api/auth/**")
						.uri("lb://auth-service"))
				
				// Products Service Routes
				.route("prod-service", r -> r.path("/api/products/**")
						.uri("lb://prod-service"))
				
				// File Service Routes
				.route("file-service", r -> r.path("/api/files/**")
						.uri("lb://file-service"))
				
				// Orders Service Routes
				.route("order-service", r -> r.path("/api/orders/**")
						.uri("lb://order-service"))
				
				// Stats Service Routes
				.route("stats-service", r -> r.path("/api/stats/**")
						.uri("lb://stats-service"))
				
				// Health check route
				.route("health", r -> r.path("/health")
						.uri("http://localhost:8080/actuator/health"))
				
				.build();
	}

	@Bean
	public CorsWebFilter corsWebFilter() {
		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
		corsConfig.setMaxAge(3600L);
		corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		corsConfig.setAllowedHeaders(Arrays.asList("*"));
		corsConfig.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig);

		return new CorsWebFilter(source);
	}
}