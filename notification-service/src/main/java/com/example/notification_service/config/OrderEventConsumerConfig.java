package com.example.notification_service.config;

import com.example.notification_service.events.CustomerEvent;
import com.example.notification_service.events.OrderEvent;
import com.example.notification_service.events.OrderEventConsumer;
import com.example.notification_service.util.MessageConverter;
import com.example.notification_service.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

/**
 * The Notification-Microservice gonna consume OrderEvents
 * It will receive the events wrapped into Messages
 * will convert them into custom Records
 * consume the Records messages, then acknowledge
 */


@Configuration
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumerConfig {
    private final OrderEventConsumer orderEventConsumer;

    @Bean
    public Consumer<Flux<Message<OrderEvent>>> orderEventConsumer() {
        return flux -> flux
                .map(MessageConverter.toRecord())
                .doOnNext(record -> log.info("The Notification Service Received Order Event: {}", Util.write(record.message())))
                .concatMap(record -> orderEventConsumer.consume(record.message())
                        .doOnSuccess(x -> record.receiverOffset().acknowledge())
                )
                .doOnError(ex -> log.warn("Error while processing the order-event: {}", ex.getMessage()))
                .subscribe();
    }
}
