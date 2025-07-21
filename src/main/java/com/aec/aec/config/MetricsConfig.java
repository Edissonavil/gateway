package com.aec.aec.config;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> {
            registry.config()
                    .meterFilter(MeterFilter.deny(id -> {
                        String name = id.getName();
                        // Deshabilitar métricas problemáticas en contenedores Alpine
                        return name.startsWith("system.cpu") || 
                               name.startsWith("process.cpu") ||
                               name.startsWith("system.load") ||
                               name.equals("process.uptime") ||
                               name.equals("process.start.time");
                    }))
                    .commonTags("application", "aec-gateway", "service", "gateway");
        };
    }
}