package com.example.customer_service.events;

import reactor.core.publisher.Flux;

public interface CustomerEventPublisher {

    Flux<CustomerEvent> publish();
}
