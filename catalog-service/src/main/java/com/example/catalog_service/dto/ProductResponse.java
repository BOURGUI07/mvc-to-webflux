package com.example.catalog_service.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ProductResponse(
        Long id, String code, String name, String description, String imageUrl, BigDecimal price) {}
