package com.example.shipping_service.events;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public sealed interface ShippingEvent extends OrderSaga {

    @Builder
    record Ready(UUID orderId,
                    UUID shipping) implements ShippingEvent {}

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
