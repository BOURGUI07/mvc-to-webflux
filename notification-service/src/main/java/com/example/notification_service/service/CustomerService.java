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

/**
 * Since the Customer-Microservice only implements POST requests regarding customer states
 * then the customer-service here is responsible for saving the newlyCreatedCustomers into the DB
 * it only save the customerId, username and email
 */


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepo repo;

    /**
     * Received the CreatedCustomerEvent
     * make sure the customer doesn't already exist
     * convert the event into an entity
     * save the entity.
     */

    @Transactional
    public Mono<Void> saveCustomer(CustomerEvent.Created event) {
        return repo.existsByCustomerId(event.customerId())
                .doOnNext(exists -> log.info("Does Customer Exists? {}",exists))
                .filter(Predicate.not(b->b))
                .doOnDiscard(Boolean.class, x -> log.info("Discard customer event: {}", Util.write(event)))
                .flatMap(x ->repo.save(Mapper.toCustomer().apply(event)))
                .doOnNext(c -> log.info("Saved New Customer: {}", Util.write(c)))
                .then();
    }
}
