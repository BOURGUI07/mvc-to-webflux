package com.example.order_service.dto;

import com.example.order_service.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record OrderPaymentDTO(UUID orderId, UUID paymentId, String message, PaymentStatus status,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt) {}
