package com.example.notification_service.consumer;

import com.example.notification_service.events.OrderEvent;
import com.example.notification_service.events.OrderEventConsumer;
import com.example.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Based on the order-event type, the notification-microservice gonna consume the events
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumerImpl implements OrderEventConsumer {
    private final NotificationService service;

    @Override
    public Mono<Void> handle(OrderEvent.Cancelled event) {
        return service.sendEmailCancelled(event);
    }

    @Override
    public Mono<Void> handle(OrderEvent.Completed event) {
        return service.sendEmailCompleted(event);
    }

    @Override
    public Mono<Void> handle(OrderEvent.Created event) {
        return service.sendEmailCreated(event);
    }
}
