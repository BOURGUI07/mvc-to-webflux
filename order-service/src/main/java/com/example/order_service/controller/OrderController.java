package com.example.order_service.controller;

import com.example.order_service.dto.OrderDTO;
import com.example.order_service.service.OrderService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public Mono<OrderDTO.Response> placeOrder(@RequestBody Mono<OrderDTO.Request> request) {
        return service.placeOrder(request);
    }
}
