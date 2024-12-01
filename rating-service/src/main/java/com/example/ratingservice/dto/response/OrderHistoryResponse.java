package com.example.ratingservice.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;


@Builder
public record OrderHistoryResponse(
        Long id,
        UUID orderId,
        Long productId,
        Long customerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
