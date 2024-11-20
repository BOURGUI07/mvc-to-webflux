package com.example.catalog_service.events;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

public sealed interface InventoryEvent extends OrderSaga{

    @Builder
    record Deducted(UUID orderId,
                   UUID inventoryId,
                   Long productId,
                   BigDecimal price,
                   Integer deductedQty) implements InventoryEvent {}

    @Builder
    record Restored(UUID orderId,
                    UUID inventoryId,
                    Long productId,
                    Integer restoredQty) implements InventoryEvent {}

    @Builder
    record Declined(UUID orderId,
                    Long productId,
                    Integer declinedQty,
                    String reason) implements InventoryEvent {}
}
