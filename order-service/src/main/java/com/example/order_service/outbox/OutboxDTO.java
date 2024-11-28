package com.example.order_service.outbox;

import com.example.order_service.events.OrderSaga;
import lombok.Builder;

@Builder
public record OutboxDTO<T extends OrderSaga>(Long correlationId, T event) {}
