package com.example.notification_service.consumer;

import com.example.notification_service.events.CustomerEvent;
import com.example.notification_service.events.CustomerEventConsumer;
import com.example.notification_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerEventConsumerImpl implements CustomerEventConsumer {

    private final CustomerService service;

    @Override
    public Mono<Void> handle(CustomerEvent.Created e) {
        return service.saveCustomer(e);
    }
}
