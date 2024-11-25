package com.example.order_service.dto;

import com.example.order_service.enums.InventoryStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderInventoryDTO(
        UUID orderId,
        UUID inventoryId,
        String message,
        InventoryStatus status
) {
}
