package com.example.order_service.controller;

import com.example.order_service.dto.OrderDTO;
import com.example.order_service.dto.OrderDetails;
import com.example.order_service.service.OrderService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
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

    @GetMapping("/{orderId}")
    public Mono<OrderDetails> getOrderDetails(@PathVariable UUID orderId) {
        return service.getOrderDetails(orderId);
    }

    @GetMapping
    public Flux<OrderDTO.Response> getAllOrders() {
        return service.findAll();
    }
}
