package com.example.catalog_service.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductCreationRequest(
        String code,
        String name,
        String description,
        String imageUrl,
        BigDecimal price
) {
}
