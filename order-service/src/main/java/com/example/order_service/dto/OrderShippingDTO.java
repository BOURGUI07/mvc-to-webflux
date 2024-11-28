package com.example.order_service.dto;

import com.example.order_service.enums.ShippingStatus;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record OrderShippingDTO(
        UUID orderId, UUID shippingId, String message, ShippingStatus status, Instant deliveryDate) {}
