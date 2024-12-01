package com.example.ratingservice.events;

import java.util.UUID;

public sealed interface OrderEvent {

    UUID orderId();
    Long customerId();
    Long productId();

    record Completed(
            UUID orderId,
            Long customerId,
            Long productId
    ) implements OrderEvent {}
}
