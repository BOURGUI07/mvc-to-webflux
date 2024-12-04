package com.example.ratingservice.mapper;

import com.example.ratingservice.dto.response.OrderHistoryResponse;
import com.example.ratingservice.entity.OrderHistory;
import com.example.ratingservice.events.OrderEvent;
import java.util.function.Function;

public class OrderMapper {

    public static Function<OrderEvent.Completed, OrderHistory> toEntity() {
        return event -> OrderHistory.builder()
                .orderId(event.orderId())
                .customerId(event.customerId())
                .productId(event.productId())
                .build();
    }

    public static Function<OrderHistory, OrderHistoryResponse> toDto() {
        return entity -> OrderHistoryResponse.builder()
                .id(entity.getId())
                .orderId(entity.getOrderId())
                .customerId(entity.getCustomerId())
                .productId(entity.getProductId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
