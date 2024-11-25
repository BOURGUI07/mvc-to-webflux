package com.example.order_service.dto;

import com.example.order_service.enums.ShippingStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record OrderShippingDTO(
        UUID orderId,
        UUID shippingId,
        String message,
        ShippingStatus status,
        Instant deliveryDate
) {
}
