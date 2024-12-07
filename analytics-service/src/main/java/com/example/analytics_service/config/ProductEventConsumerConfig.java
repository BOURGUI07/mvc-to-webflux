package com.example.analytics_service.config;

import com.example.analytics_service.consumer.ProductViewConsumerImpl;
import com.example.analytics_service.events.ProductEvent;
import com.example.analytics_service.util.MessageConverter;
import com.example.analytics_service.util.Record;
import com.example.analytics_service.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The Catalog-Microservice will emit product-events
 * among these events, ViewedProductEvents
 * The Analytics-Microservice gonna receive those events wrapped in Message formats
 * Then it will convert them into custom Records
 * Consumer the Record message (or the Message payload)
 * Then acknowledge the messages.
 */

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ProductEventConsumerConfig {

    private final ProductViewConsumerImpl productEventConsumer;


    @Bean
    public Consumer<Flux<Message<ProductEvent>>> productViewConsumer(){
        return flux -> flux
                .doOnNext(msg -> log.info("The Analytics Service Received Product View Event: {}", Util.write(msg.getPayload())))
                .map(MessageConverter.toRecord())
                .concatMap(record ->
                        productEventConsumer.handle(record.message())
                                .doOnSuccess(__ -> record.receiverOffset().acknowledge())
                )
                .subscribe();
    }

}
