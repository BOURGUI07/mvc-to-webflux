package com.example.analytics_service.events;

import lombok.Builder;


public sealed interface ProductEvent {

    String code();

    /**
     * This ViewedProductEvent gonna be emitted by the catalog-microservice
     */

    @Builder
    record View(String code) implements ProductEvent {}
}
