package com.example.analytics_service.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProductViewDTO(
        String productCode,
        Long viewCount,
        LocalDateTime createdAt,
        LocalDateTime updateAt
) {
}
