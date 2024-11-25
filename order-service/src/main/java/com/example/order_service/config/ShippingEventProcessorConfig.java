package com.example.order_service.config;

import com.example.order_service.events.OrderEvent;
import com.example.order_service.events.PaymentEvent;
import com.example.order_service.events.ShippingEvent;
import com.example.order_service.events.ShippingEventProcessor;
import com.example.order_service.util.MessageConverter;
import com.example.order_service.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.function.Function;
import java.util.function.UnaryOperator;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ShippingEventProcessorConfig {

    private final ShippingEventProcessor processor;


    @Bean
    public Function<Flux<Message<ShippingEvent>>,Flux<Message<OrderEvent>>> shippingEventProcessor() {
        return flux -> flux
                .doOnNext(msg -> log.info("The Order Service Received Shipping Event: {}", Util.write(msg.getPayload())))
                .map(MessageConverter.toRecord())
                .concatMap(record -> processor.process(record.message())
                        .doOnSuccess(__-> record.receiverOffset().acknowledge())
                )
                .map(MessageConverter.toMessage())
                .doOnNext(msg -> log.info("The Order Service Product Order Event: {}", Util.write(msg.getPayload())));
    }



}
