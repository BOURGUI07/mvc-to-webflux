package com.example.customer_service.dto;

import com.example.customer_service.domain.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public sealed interface PaymentDTO {

    UUID orderId();
    BigDecimal amount();
    Long customerId();

    @Builder
    record Request(
            UUID orderId,
            BigDecimal amount,
            Long customerId
    )implements PaymentDTO {}

    @Builder
    record Response(
            UUID orderId,
            UUID paymentId,
            BigDecimal amount,
            Long customerId,
            PaymentStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    )implements PaymentDTO {}
}
