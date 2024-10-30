package com.example.catalog_service.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ProductCreationRequest(String code, String name, String description, String imageUrl, BigDecimal price) {}
