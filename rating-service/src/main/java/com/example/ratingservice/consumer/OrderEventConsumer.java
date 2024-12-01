package com.example.ratingservice.consumer;

import com.example.ratingservice.events.OrderEvent;
import reactor.core.publisher.Mono;

public interface OrderEventConsumer {

    default Mono<Void> consume(OrderEvent orderEvent){
        return switch (orderEvent){
            case OrderEvent.Completed e -> handle(e);
        };
    }

     Mono<Void> handle(OrderEvent.Completed event);


}
