package com.example.order_service.dto;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@Builder
@ConfigurationProperties(prefix = "order.service")
public record OrderServiceProperties(
        String name,
        URI exceptionGeneral,
        URI exceptionBadRequest
) {

}
