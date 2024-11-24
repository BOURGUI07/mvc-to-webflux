package com.example.analytics_service.events;

import lombok.Builder;

public sealed interface ProductEvent {

    String code();

    @Builder
    record View(String code) implements ProductEvent {}
}
