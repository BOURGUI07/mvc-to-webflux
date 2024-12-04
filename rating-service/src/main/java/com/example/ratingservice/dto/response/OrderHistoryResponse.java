package com.example.ratingservice.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record OrderHistoryResponse(
        Long id, UUID orderId, Long productId, Long customerId, LocalDateTime createdAt, LocalDateTime updatedAt) {}
