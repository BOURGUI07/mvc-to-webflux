package com.example.notification_service.events;

import reactor.core.publisher.Mono;

public interface CustomerEventConsumer extends EventConsumer<CustomerEvent> {

    @Override
    default Mono<Void> consume(CustomerEvent event) {
        return switch (event){
            case CustomerEvent.Created e -> handle(e);
        };
    }


    Mono<Void> handle(CustomerEvent.Created e);
}
