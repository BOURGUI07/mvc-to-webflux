package com.example.ratingservice.events;

import lombok.Builder;

import java.util.UUID;

public sealed interface OrderEvent {

    UUID orderId();
    Long customerId();
    Long productId();

    @Builder
    record Completed(
            UUID orderId,
            Long customerId,
            Long productId
    ) implements OrderEvent {}
}
