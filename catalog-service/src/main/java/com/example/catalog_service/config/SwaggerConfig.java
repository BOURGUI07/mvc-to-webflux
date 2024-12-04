package com.example.catalog_service.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("catalog-service")
                .packagesToScan("com.example.catalog_service")
                .pathsToMatch("/api/**")
                .build();
    }
}
