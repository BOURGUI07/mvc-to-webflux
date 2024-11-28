package com.example.order_service.dto;

import java.net.URI;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Builder
@ConfigurationProperties(prefix = "order.service")
public record OrderServiceProperties(String name, URI exceptionGeneral, URI exceptionBadRequest) {}
