package com.example.catalog_service.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.net.URI;

@ConfigurationProperties(prefix = "catalog.service")
public record CatalogServiceProperties(
        @DefaultValue("10")
        int defaultPageSize,
        @DefaultValue("catalog-service")
        String name,
        URI exceptionNotFound,
        URI exceptionGeneral
) {}
