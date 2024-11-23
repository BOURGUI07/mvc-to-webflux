package com.example.customer_service.publisher;

import com.example.customer_service.events.CustomerEvent;
import com.example.customer_service.events.CustomerEventPublisher;
import com.example.customer_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerEventPublisherImpl implements CustomerEventPublisher {
    private final CustomerService service;

    @Override
    public Flux<CustomerEvent> publish() {
        return service.events();
    }
}
