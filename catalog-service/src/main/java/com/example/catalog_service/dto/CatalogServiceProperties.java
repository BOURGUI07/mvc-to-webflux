package com.example.catalog_service.dto;

import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "catalog.service")
public record CatalogServiceProperties(
        @DefaultValue("10") int defaultPageSize,
        @DefaultValue("catalog-service") String name,
        @DefaultValue("https://api.bookstore.com/errors/not-found")
        URI exceptionNotFound,
        @DefaultValue("https://api.bookstore.com/errors/general")
        URI exceptionGeneral) {}
