package com.example.shipping_service.config;

import com.example.shipping_service.events.OrderEvent;
import com.example.shipping_service.events.OrderEventProcessor;
import com.example.shipping_service.events.ShippingEvent;
import com.example.shipping_service.util.MessageConverter;
import com.example.shipping_service.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class OrderEventProcessorConfig {

    private final OrderEventProcessor orderEventProcessor;

    @Bean
    public Function<Flux<Message<OrderEvent>>, Flux<Message<ShippingEvent>>> processor(){
        return flux -> flux
                .doOnNext(message -> log.info("The Shipping-Service Received OrderEvent: {}", Util.write(message.getPayload())))
                .map(MessageConverter.toRecord())
                .concatMap(record -> orderEventProcessor.process(record.message())
                        .doOnSuccess(x -> record.receiverOffset().acknowledge())
                )
                .map(MessageConverter.toMessage())
                .doOnNext(msg -> log.info("The Shipping-Service Produced ShippingEvent: {}", Util.write(msg.getPayload())));
    }
}
