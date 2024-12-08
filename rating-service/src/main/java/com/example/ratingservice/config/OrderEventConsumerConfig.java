package com.example.ratingservice.config;

import com.example.ratingservice.consumer.OrderEventConsumer;
import com.example.ratingservice.events.OrderEvent;
import com.example.ratingservice.util.MessageConverter;
import com.example.ratingservice.util.Util;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

/**
 * The moment a order is completed, the order-microservice will emit
 * order-events. Among the microservices that will consume those events is
 * the rating-microservice. So that whenever a user wants to write a rating for a product
 * the rating service will be able to validate if the productId and customerId were part of some order.
 */


@Configuration
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumerConfig {
    private final OrderEventConsumer consumer;

    @Bean
    public Consumer<Flux<Message<OrderEvent>>> completedOrderEventConsumer() {
        return flux -> flux.doOnNext(
                        msg -> log.info("The Rating-Service Received Order Event: {}", Util.write(msg.getPayload())))
                .map(MessageConverter.toRecord())
                .concatMap(record -> consumer.consume(record.message())
                        .doOnSuccess(__ -> record.receiverOffset().acknowledge()))
                .subscribe();
    }
}
