package com.example.order_service.publisher;


import com.example.order_service.events.OrderEvent;
import com.example.order_service.events.OrderEventPublisher;
import com.example.order_service.service.OrderService;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisherImpl implements OrderEventPublisher {
    private final OrderService service;

    @Override
    public Flux<OrderEvent> publish() {
        return service.events();
    }
}
