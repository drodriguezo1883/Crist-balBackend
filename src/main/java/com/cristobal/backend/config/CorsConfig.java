package com.cristobal.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    // Origen principal. Admite una lista separada por comas para uso local/flexible.
    @Value("${app.cors.allowed-origin:http://localhost:4200}")
    private String allowedOrigin;

    // Origen(es) adicionales en una variable APARTE (sin comas), para evitar
    // depender de que el proveedor de hosting entregue bien un valor con coma.
    @Value("${app.cors.allowed-origin-extra:}")
    private String allowedOriginExtra;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        String[] origins = origenesPermitidos();
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(origins)
                        .allowedMethods("POST")
                        .allowedHeaders("Content-Type");
            }
        };
    }

    private String[] origenesPermitidos() {
        List<String> origenes = new ArrayList<>(Arrays.asList(allowedOrigin.split("\\s*,\\s*")));
        if (allowedOriginExtra != null && !allowedOriginExtra.isBlank()) {
            origenes.addAll(Arrays.asList(allowedOriginExtra.split("\\s*,\\s*")));
        }
        return origenes.stream()
                .map(String::trim)
                .filter(o -> !o.isEmpty())
                .distinct()
                .toArray(String[]::new);
    }
}
