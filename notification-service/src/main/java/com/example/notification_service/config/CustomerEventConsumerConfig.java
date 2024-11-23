package com.example.notification_service.config;

import com.example.notification_service.events.CustomerEvent;
import com.example.notification_service.events.CustomerEventConsumer;
import com.example.notification_service.util.MessageConverter;
import com.example.notification_service.util.Util;
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
public class CustomerEventConsumerConfig {
    private final CustomerEventConsumer customerEventConsumer;

    @Bean
    public Consumer<Flux<Message<CustomerEvent>>> customerEventConsumer() {
        return flux -> flux
                .map(MessageConverter.toRecord())
                .doOnNext(record -> log.info("The Notification Service Received Customer Event: {}", Util.write(record.message())))
                .concatMap(record -> customerEventConsumer.consume(record.message())
                        .doOnSuccess(x -> record.receiverOffset().acknowledge())
                )
                .doOnError(ex -> log.info("Error while consuming event: {}", ex.getMessage()))
                .subscribe();
    }
}
