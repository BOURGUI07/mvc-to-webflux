package com.example.catalog_service.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "catalog")
public record CatalogServiceProperties(
        @DefaultValue("10")
        int serviceDefaultPageSize
) {
}
