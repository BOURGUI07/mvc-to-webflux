package com.example.shipping_service.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "shipping.service")
public record ShippingServiceProperties(
        URI exceptionGeneral,
        String name
) {

}
