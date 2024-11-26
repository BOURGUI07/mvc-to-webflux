package com.example.order_service.dto;

import com.example.order_service.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

public sealed interface OrderDTO {

    Long productId();
    Integer quantity();
    Long customerId();

    @Builder
    record Request(
            Long productId,
            Long customerId,
            Integer quantity
    ) implements OrderDTO {}

    @Builder
    record Response(
            BigDecimal amount,
            BigDecimal price,
            UUID orderId,
            OrderStatus status,
            Long productId,
            Long customerId,
            Integer quantity
    ) implements OrderDTO {}
}