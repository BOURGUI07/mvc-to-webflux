package com.example.customer_service.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.net.URI;

@ConfigurationProperties(prefix = "customer.service")
public record CustomerServiceProperties(
        @DefaultValue("10") int defaultPageSize,
        @DefaultValue("catalog-service") String name,
        @DefaultValue("https://api.bookstore.com/errors/not-found")
        URI exceptionNotFound,
        @DefaultValue("https://api.bookstore.com/errors/bad-request")
        URI exceptionBadRequest,
        @DefaultValue("https://api.bookstore.com/errors/general")
        URI exceptionGeneral
) {
}
