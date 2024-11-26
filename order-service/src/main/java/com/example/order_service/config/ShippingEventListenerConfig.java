package com.example.order_service.config;

import com.example.order_service.events.OrderEvent;
import com.example.order_service.events.ShippingEvent;
import com.example.order_service.listener.ShippingEventListener;
import com.example.order_service.util.MessageConverter;
import com.example.order_service.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ShippingEventListenerConfig {

    private final ShippingEventListener listener;


    @Bean
    public Function<Flux<Message<ShippingEvent>>, Mono<Void>> shippingEventListener() {
        return flux -> flux
                .doOnNext(msg -> log.info("The Order Service Received Shipping Event: {}", Util.write(msg.getPayload())))
                .map(MessageConverter.toRecord())
                .concatMap(record -> listener.listen(record.message())
                        .doOnSuccess(__-> record.receiverOffset().acknowledge())
                )
                .then();
    }



}
