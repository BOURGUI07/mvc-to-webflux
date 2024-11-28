package com.example.order_service.mapper;

import com.example.order_service.dto.OrderShippingDTO;
import com.example.order_service.entity.OrderShipping;
import com.example.order_service.enums.ShippingStatus;
import com.example.order_service.events.ShippingEvent;
import java.util.function.Function;

public class ShippingMapper {

    public static Function<ShippingEvent.Ready, OrderShippingDTO> fromReadyToShippingDTO() {
        return event -> OrderShippingDTO.builder()
                .shippingId(event.shippingId())
                .status(ShippingStatus.PENDING)
                .orderId(event.orderId())
                .build();
    }

    public static Function<ShippingEvent.Declined, OrderShippingDTO> fromDeclinedToShippingDTO() {
        return event -> OrderShippingDTO.builder()
                .status(ShippingStatus.DECLINED)
                .orderId(event.orderId())
                .message(event.reason())
                .build();
    }

    public static Function<ShippingEvent.Scheduled, OrderShippingDTO> fromScheduledToShippingDTO() {
        return event -> OrderShippingDTO.builder()
                .status(ShippingStatus.SCHEDULED)
                .orderId(event.orderId())
                .deliveryDate(event.deliveryDate())
                .shippingId(event.shippingId())
                .build();
    }

    public static Function<ShippingEvent.Cancelled, OrderShippingDTO> fromCancelledToShippingDTO() {
        return event -> OrderShippingDTO.builder()
                .status(ShippingStatus.CANCELLED)
                .orderId(event.orderId())
                .shippingId(event.shippingId())
                .build();
    }

    public static Function<OrderShipping, OrderShippingDTO> toDTO() {
        return entity -> OrderShippingDTO.builder()
                .shippingId(entity.getShippingId())
                .orderId(entity.getOrderId())
                .status(entity.getStatus())
                .message(entity.getMessage())
                .deliveryDate(entity.getDeliveryDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static Function<OrderShippingDTO, OrderShipping> toEntity() {
        return dto -> OrderShipping.builder()
                .orderId(dto.orderId())
                .status(dto.status())
                .message(dto.message())
                .deliveryDate(dto.deliveryDate())
                .shippingId(dto.shippingId())
                .build();
    }
}
