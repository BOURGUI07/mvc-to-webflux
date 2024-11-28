package com.example.order_service.config;

import com.example.order_service.events.OrderEvent;
import com.example.order_service.outbox.OutboxDTO;
import com.example.order_service.publisher.OrderEventOutboxService;
import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.channel.FluxMessageChannel;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderResult;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisherConfig {
    private final OrderEventOutboxService service;

    @Bean
    public Supplier<Flux<Message<OrderEvent>>> orderEventProducer() {
        return () -> service.publish()
                .repeatWhen(f -> f.delayElements(Duration.ofMillis(1000)))
                .map(toMessage());
    }

    @Bean
    @SuppressWarnings("unchecked")
    public FluxMessageChannel orderEventResults() {
        var channel = new FluxMessageChannel();
        Flux.from(channel)
                .map(msg -> (SenderResult<Long>) msg.getPayload()) // because correlationId is of type Long
                .map(SenderResult::correlationMetadata)
                .bufferTimeout(
                        1000,
                        Duration.ofMillis(100)) // ensure this time less than time configured in repeatWhen() above
                .flatMap(service::deleteEvents)
                .subscribe();

        return channel;
    }

    public static <T extends OrderEvent> Function<OutboxDTO<T>, Message<T>> toMessage() {
        return outboxDTO -> MessageBuilder.withPayload(outboxDTO.event())
                .setHeader(KafkaHeaders.KEY, outboxDTO.event().orderId().toString())
                .setHeader(IntegrationMessageHeaderAccessor.CORRELATION_ID, outboxDTO.correlationId())
                .build();
    }
}
