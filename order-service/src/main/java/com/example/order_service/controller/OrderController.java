package com.example.order_service.controller;

import com.example.order_service.dto.OrderDTO;
import com.example.order_service.dto.OrderDetails;
import com.example.order_service.service.OrderService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.example.order_service.util.Constants.PublicApiUrls.ORDER_BASE_URL;

@RestController
@RequestMapping(ORDER_BASE_URL)
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public Mono<ResponseEntity<OrderDTO.Response>> placeOrder(@RequestBody Mono<OrderDTO.Request> request) {
        return service.placeOrder(request)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }

    @GetMapping("/{orderId}")
    public Mono<ResponseEntity<OrderDetails>> getOrderDetails(@PathVariable UUID orderId) {
        return service.getOrderDetails(orderId)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<OrderDTO.Response>>> getAllOrders() {
        return Mono.fromSupplier(() -> ResponseEntity.ok(service.findAll()));
    }
}
