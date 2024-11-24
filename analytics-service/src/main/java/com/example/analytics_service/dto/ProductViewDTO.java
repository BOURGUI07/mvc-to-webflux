package com.example.analytics_service.dto;

import lombok.Builder;

@Builder
public record ProductViewDTO(
        String productCode,
        Long viewCount
) {
}
