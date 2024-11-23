package com.example.notification_service.events;

import reactor.core.publisher.Mono;

public interface EventConsumer<T> {

    Mono<Void> consume(T event);
}
