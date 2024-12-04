package com.example.ratingservice.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("rating-service")
                .packagesToScan("com.example.ratingservice")
                .pathsToMatch("/api/**")
                .build();
    }
}
