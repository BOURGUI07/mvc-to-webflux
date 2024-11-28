package com.example.order_service.config;

import com.example.order_service.events.InventoryEvent;
import com.example.order_service.listener.InventoryEventListener;
import com.example.order_service.util.MessageConverter;
import com.example.order_service.util.Util;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListenerConfig {
    private final InventoryEventListener listener;

    @Bean
    public Function<Flux<Message<InventoryEvent>>, Mono<Void>> inventoryEventListener() {
        return flux -> flux.doOnNext(
                        msg -> log.info("The Order Service Received Inventory Event: {}", Util.write(msg.getPayload())))
                .map(MessageConverter.toRecord())
                .concatMap(record -> listener.listen(record.message())
                        .doOnSuccess(__ -> record.receiverOffset().acknowledge()))
                .then();
    }
}
