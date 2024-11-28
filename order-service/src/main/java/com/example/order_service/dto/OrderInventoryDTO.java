package com.example.order_service.dto;

import com.example.order_service.enums.InventoryStatus;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record OrderInventoryDTO(UUID orderId, UUID inventoryId, String message, InventoryStatus status,
                                LocalDateTime createdAt,
                                LocalDateTime updatedAt) {}
