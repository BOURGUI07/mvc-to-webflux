package com.example.customer_service.config;

import com.example.customer_service.events.CustomerEvent;
import com.example.customer_service.events.CustomerEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The Customer-Microservice gonna emit CustomerEvents.
 * Before emitting them, it will wrap them in Message format
 * As the Notification-Microservice will eventually need the customer infos
 * such as username and email, it will consumer these customer-events
 * to be able to send emails.
 */

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CustomerEventPublisherConfig {
    private final CustomerEventPublisher customerEventPublisher;


    @Bean
    public Supplier<Flux<Message<CustomerEvent>>> customerEventProducer() {
        return () -> customerEventPublisher.publish()
                .map(toMessage())
                .doOnNext(msg -> log.info("The Customer Service Produced Customer Event: {}", msg.toString()));
    }

    private Function<CustomerEvent,Message<CustomerEvent>> toMessage(){
        return event -> MessageBuilder.withPayload(event)
                .setHeader(KafkaHeaders.KEY,event.customerId().toString())
                .build();
    }
}
