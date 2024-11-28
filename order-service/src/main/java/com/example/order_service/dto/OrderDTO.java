package com.example.order_service.dto;

import com.example.order_service.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

public sealed interface OrderDTO {

    Long productId();

    Integer quantity();

    Long customerId();

    @Builder
    record Request(Long productId, Long customerId, Integer quantity) implements OrderDTO {}

    @Builder
    record Response(
            BigDecimal amount,
            BigDecimal price,
            UUID orderId,
            OrderStatus status,
            Long productId,
            Long customerId,
            Integer quantity,
            LocalDateTime createdAt,
            LocalDateTime updatedAt)
            implements OrderDTO {}
}
