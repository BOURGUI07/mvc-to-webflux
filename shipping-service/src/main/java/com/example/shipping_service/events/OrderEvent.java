package com.example.shipping_service.events;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

public sealed interface OrderEvent extends OrderSaga{

    @Builder
    record Created(UUID orderId,
                   Long productId,
                   BigDecimal price,
                   Integer quantity,
                   Long customerId) implements OrderEvent {
        public BigDecimal amount() {
            return price.multiply(BigDecimal.valueOf(quantity));
        }
    }


    @Builder
    record Cancelled(UUID orderId,Long customerId) implements OrderEvent {}

    @Builder
    record Completed(UUID orderId,Long customerId) implements OrderEvent {}
}
