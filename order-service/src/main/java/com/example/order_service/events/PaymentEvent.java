package com.example.order_service.events;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

public sealed interface PaymentEvent extends OrderSaga{
    @Builder
    record Deducted(UUID orderId,
                    UUID paymentId,
                    Long customerId,
                    BigDecimal deductedAmount) implements PaymentEvent {}

    @Builder
    record Refunded(UUID orderId,
                    UUID paymentId,
                    Long customerId,
                    BigDecimal refundedAmount) implements PaymentEvent {}

    @Builder
    record Declined(UUID orderId,
                    Long customerId,
                    BigDecimal refundedAmount,
                    String reason) implements PaymentEvent {}
}