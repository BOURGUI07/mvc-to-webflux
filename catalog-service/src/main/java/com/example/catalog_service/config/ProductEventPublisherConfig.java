package com.example.catalog_service.config;

import com.example.catalog_service.events.ProductEvent;
import com.example.catalog_service.publisher.ProductEventPublisher;
import com.example.catalog_service.util.MessageConverter;
import com.example.catalog_service.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;


/**
 * Produce product-events.
 * Viewed product events will be consumed by the analytic-microservice
 * While deleted, created, and updated product events will be consumed by the order-microservice
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisherConfig {
    private final ProductEventPublisher publisher;


    @Bean
    public Supplier<Flux<Message<ProductEvent>>> producer(){
        return () -> publisher.publish()
                .map(MessageConverter.toProductEventMessage())
                .doOnNext(msg -> log.info("Catalog-Service Produced Product Event: {}", Util.write(msg.getPayload())));
    }

}
