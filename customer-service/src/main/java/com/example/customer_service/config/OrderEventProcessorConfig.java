package com.example.customer_service.config;

import com.example.customer_service.events.OrderEvent;
import com.example.customer_service.events.OrderEventProcessor;
import com.example.customer_service.events.PaymentEvent;
import com.example.customer_service.util.MessageConverter;
import com.example.customer_service.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OrderEventProcessorConfig {
    private final OrderEventProcessor orderEventProcessor;

    @Bean
    public Function<Flux<Message<OrderEvent>>, Flux<Message<PaymentEvent>>> processor(){
        return flux -> flux
                .doOnNext(message -> log.info("The Customer-Service Received OrderEvent: {}", Util.write(message.getPayload())))
                .map(MessageConverter.toRecord())
                .concatMap(record -> orderEventProcessor.process(record.message())
                        .doOnSuccess(x -> record.receiverOffset().acknowledge())
                )
                .map(MessageConverter.toMessage())
                .doOnNext(event -> log.info("The Customer-Service Produced PaymentEvent: {}", Util.write(event.getPayload())));
    }
}
