package com.example.order_service.events;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

public sealed interface ShippingEvent extends OrderSaga{
    @Builder
    record Ready(UUID orderId,
                 UUID shippingId) implements ShippingEvent {}

    @Builder
    record Scheduled(UUID orderId,
                     UUID shippingId,
                     Instant deliveryDate) implements ShippingEvent {}

    @Builder
    record Declined(UUID orderId,
                    String reason) implements ShippingEvent {}


    @Builder
    record Cancelled(UUID orderId,
                     UUID shippingId) implements ShippingEvent {}
}
