package com.example.notification_service.service;

import com.example.notification_service.entity.Customer;
import com.example.notification_service.events.CustomerEvent;
import com.example.notification_service.mapper.Mapper;
import com.example.notification_service.repo.CustomerRepo;
import com.example.notification_service.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepo repo;

    @Transactional
    public Mono<Void> saveCustomer(CustomerEvent.Created event) {
        return repo.existsByCustomerId(event.customerId())
                .filter(Predicate.not(b->b))
                .switchIfEmpty(Mono.empty())
                .then(repo.save(Mapper.toCustomer().apply(event)))
                .doOnNext(c -> log.info("Saved New Customer: {}", Util.write(c)))
                .then();
    }
}
