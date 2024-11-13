package com.example.order_service.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.net.URI;

@ConfigurationProperties(prefix = "order.service")
public record OrderServiceProperties(
        @DefaultValue("order-service") String name,
        @DefaultValue("10") int defaultPageSize,
        @DefaultValue("https://api.bookstore.com/errors/not-found") URI exceptionNotFound,
        @DefaultValue("https://api.bookstore.com/errors/general") URI exceptionGeneral
) {
        }
