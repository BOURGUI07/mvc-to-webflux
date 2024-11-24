package com.example.order_service.config;

import com.example.order_service.events.ProductEvent;
import com.example.order_service.events.ProductEventConsumer;
import com.example.order_service.util.MessageConverter;
import com.example.order_service.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ProductEventConsumerConfig {

    private final ProductEventConsumer consumer;


    @Bean
    public Consumer<Flux<Message<ProductEvent>>> productEventConsumer() {
        return flux -> flux
                .doOnNext(msg -> log.info("The Order-Service Received Product Event: {}", Util.write(msg.getPayload())))
                .map(MessageConverter.toRecord())
                .concatMap(record -> consumer.consume(record.message())
                        .doOnSuccess(__ -> record.receiverOffset().acknowledge())
                )
                .subscribe();
    }
}
