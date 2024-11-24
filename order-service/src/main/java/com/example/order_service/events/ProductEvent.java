package com.example.order_service.events;

import lombok.Builder;

import java.math.BigDecimal;

public sealed interface ProductEvent {
    String code();

    @Builder
    record Created(
            Long productId,
            String code,
            BigDecimal price
    )implements ProductEvent{}

    @Builder
    record Updated(
            String code,
            BigDecimal price
    )implements ProductEvent{}


    @Builder
    record Deleted(String code) implements ProductEvent{}

    @Builder
    record View(String code) implements ProductEvent{}
}
