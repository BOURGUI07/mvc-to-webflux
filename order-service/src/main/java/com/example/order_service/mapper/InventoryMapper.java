package com.example.order_service.mapper;

import com.example.order_service.dto.OrderInventoryDTO;
import com.example.order_service.entity.OrderInventory;
import com.example.order_service.enums.InventoryStatus;
import com.example.order_service.events.InventoryEvent;
import java.util.function.Function;

public class InventoryMapper {

    public static Function<InventoryEvent.Deducted, OrderInventoryDTO> fromDeductedToInventoryDTO() {
        return event -> OrderInventoryDTO.builder()
                .inventoryId(event.inventoryId())
                .status(InventoryStatus.DEDUCTED)
                .orderId(event.orderId())
                .build();
    }

    public static Function<InventoryEvent.Restored, OrderInventoryDTO> fromRestoredToInventoryDTO() {
        return event -> OrderInventoryDTO.builder()
                .inventoryId(event.inventoryId())
                .status(InventoryStatus.RESTORED)
                .orderId(event.orderId())
                .build();
    }

    public static Function<InventoryEvent.Declined, OrderInventoryDTO> fromDeclinedToInventoryDTO() {
        return event -> OrderInventoryDTO.builder()
                .status(InventoryStatus.DECLINED)
                .orderId(event.orderId())
                .message(event.reason())
                .build();
    }

    public static Function<OrderInventory, OrderInventoryDTO> toDTO() {
        return entity -> OrderInventoryDTO.builder()
                .inventoryId(entity.getInventoryId())
                .orderId(entity.getOrderId())
                .status(entity.getStatus())
                .message(entity.getMessage())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static Function<OrderInventoryDTO, OrderInventory> toEntity() {
        return dto -> OrderInventory.builder()
                .orderId(dto.orderId())
                .status(dto.status())
                .message(dto.message())
                .inventoryId(dto.inventoryId())
                .build();
    }
}
