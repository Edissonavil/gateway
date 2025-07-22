package com.aec.aec.config;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;

import reactor.core.publisher.Flux;

@Configuration
public class GatewayConfig {

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Bean
    public GlobalFilter preserveBodyFilter() {
        return (exchange, chain) -> {
            // Conserva el cuerpo original
            return DataBufferUtils.join(exchange.getRequest().getBody())
                    .flatMap(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);
                        String body = new String(bytes, StandardCharsets.UTF_8);
                        System.out.println("Request body preserved: " + body); // Log para debug

                        // Crea una nueva request con el cuerpo preservado
                        ServerHttpRequestDecorator decoratedRequest = new ServerHttpRequestDecorator(
                                exchange.getRequest()) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                return Flux.just(exchange.getResponse().bufferFactory().wrap(bytes));
                            }
                        };

                        return chain.filter(exchange.mutate().request(decoratedRequest).build());
                    });
        };
    }

    @Bean
    public GlobalFilter corsHeadersFilter() {
        return (exchange, chain) -> {
            exchange.getResponse().getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            exchange.getResponse().getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                    "GET,POST,PUT,DELETE,OPTIONS,PATCH");
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

    @Bean
    public GlobalFilter loggingFilter() {
        return (exchange, chain) -> {
            System.out.println("Request body: " + exchange.getRequest().getBody());
            return chain.filter(exchange);
        };
    }
}