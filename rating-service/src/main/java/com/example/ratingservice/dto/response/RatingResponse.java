package com.example.ratingservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record RatingResponse(
        Long customerId,
        Long productId,
        UUID orderId,
        String title,
        String content,
        Double value,
        Long ratingId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
