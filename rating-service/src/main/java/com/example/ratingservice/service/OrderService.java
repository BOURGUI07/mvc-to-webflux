package com.example.ratingservice.service;

import com.example.ratingservice.events.OrderEvent;
import com.example.ratingservice.exceptions.ApplicationExceptions;
import com.example.ratingservice.mapper.OrderMapper;
import com.example.ratingservice.repo.OrderHistoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderHistoryRepo repo;


    public Mono<Void> saveOrder(OrderEvent.Completed event){
        return repo.existsByOrderId(event.orderId())
                .filter(Predicate.not(b->b))
                .switchIfEmpty(ApplicationExceptions.duplicateEvent())
                .then(repo.save(OrderMapper.toEntity().apply(event)))
                .map(OrderMapper.toDto())
                .doOnNext(dto -> log.info("Order Service Layer Produced Order History: {}", dto))
                .then();
    }
}
