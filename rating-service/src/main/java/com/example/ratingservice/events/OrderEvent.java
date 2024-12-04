package com.example.ratingservice.events;

import java.util.UUID;
import lombok.Builder;

public sealed interface OrderEvent {

    UUID orderId();

    Long customerId();

    Long productId();

    @Builder
    record Completed(UUID orderId, Long customerId, Long productId) implements OrderEvent {}
}
