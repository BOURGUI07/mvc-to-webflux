package com.example.catalog_service.dto;

import com.example.catalog_service.domain.InventoryStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public sealed interface PurchaseDTO {

    Long productId();
    Integer quantity();
    UUID orderId();

    @Builder
    record Request(
            Long productId,
            Integer quantity,
            UUID orderId
    ) implements PurchaseDTO {}


    @Builder
    record Response(
            UUID inventoryId,
            BigDecimal price,
            InventoryStatus status,
            Long productId,
            Integer quantity,
            UUID orderId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) implements PurchaseDTO {}
}
