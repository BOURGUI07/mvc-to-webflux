package com.example.order_service.config;

import com.example.order_service.events.OrderEvent;
import com.example.order_service.events.OrderEventPublisher;
import com.example.order_service.util.MessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisherConfig {
    private final OrderEventPublisher orderEventPublisher;


    @Bean
    public Supplier<Flux<Message<OrderEvent>>> orderEventProducer(){
        return () -> orderEventPublisher.publish()
                .map(MessageConverter.toMessage())
                .doOnNext(msg -> log.info("Order Service Producer Order Event Created: {}",msg.toString()));
    }
}
