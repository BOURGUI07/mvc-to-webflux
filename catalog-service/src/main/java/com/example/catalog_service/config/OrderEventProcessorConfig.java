package com.example.catalog_service.config;

import com.example.catalog_service.events.InventoryEvent;
import com.example.catalog_service.events.OrderEvent;
import com.example.catalog_service.events.OrderEventProcessor;
import com.example.catalog_service.util.MessageConverter;
import com.example.catalog_service.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class OrderEventProcessorConfig {

    private final OrderEventProcessor orderEventProcessor;


    /**
     * Received Messages of Order-Events from The Order-MicroService
     * Convert the messages into Records(custom records)
     * process the Record's message (The Message Payload)
     * Acknowledge
     * The processing will return an event
     * Wrap it into Message.
     */
    @Bean
    public Function<Flux<Message<OrderEvent>>,Flux<Message<InventoryEvent>>> processor(){
        return flux -> flux
                .doOnNext(message -> log.info("The Catalog-Service Received OrderEvent: {}", Util.write(message.getPayload())))
                .map(MessageConverter.toRecord())
                .concatMap(record -> orderEventProcessor.process(record.message())
                        .doOnSuccess(x -> record.receiverOffset().acknowledge())
                )
                .map(MessageConverter.toMessage())
                .doOnNext(event -> log.info("The Catalog-Service Produced InventoryEvent: {}", Util.write(event)));
    }


}
