package com.example.ratingservice.dto.request;

import java.util.UUID;
import lombok.Builder;

@Builder
public record RatingCreationRequest(
        Long customerId, Long productId, UUID orderId, String title, String content, Double value) {}
