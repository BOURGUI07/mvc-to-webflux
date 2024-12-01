package com.example.ratingservice.dto.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RatingCreationRequest(
        Long customerId,
        Long productId,
        UUID orderId,
        String title,
        String content,
        Double value
) {
}
