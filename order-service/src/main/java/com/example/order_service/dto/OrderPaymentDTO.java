package com.example.order_service.dto;

import com.example.order_service.enums.PaymentStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderPaymentDTO(
        UUID orderId,
        UUID paymentId,
        String message,
        PaymentStatus status
) {
}
