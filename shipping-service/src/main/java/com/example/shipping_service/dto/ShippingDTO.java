package com.example.shipping_service.dto;

import com.example.shipping_service.domain.ShippingStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

public sealed interface ShippingDTO {

    @Builder
    record Request(
            UUID orderId,
            Long customerId,
            Long productId,
            Integer quantity
    ) implements ShippingDTO {}

    @Builder
    record Response(
            UUID orderId,
            Long customerId,
            Long productId,
            Integer quantity,
            UUID shippingId,
            Instant deliveryDate,
            ShippingStatus status
    ) implements ShippingDTO {}
}
