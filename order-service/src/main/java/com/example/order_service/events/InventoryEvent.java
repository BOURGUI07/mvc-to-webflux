package com.example.order_service.events;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

public sealed interface InventoryEvent extends OrderSaga {
    @Builder
    record Deducted(UUID orderId, UUID inventoryId, Long productId, BigDecimal price, Integer deductedQty)
            implements InventoryEvent {}

    @Builder
    record Restored(UUID orderId, UUID inventoryId, Long productId, Integer restoredQty) implements InventoryEvent {}

    @Builder
    record Declined(UUID orderId, Long productId, Integer declinedQty, String reason) implements InventoryEvent {}
}
