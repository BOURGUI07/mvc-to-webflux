package com.example.order_service.service;

import com.example.order_service.dto.OrderDTO;
import com.example.order_service.entity.PurchaseOrder;
import com.example.order_service.events.OrderEvent;
import com.example.order_service.mapper.OrderMapper;
import com.example.order_service.repo.ProductRepo;
import com.example.order_service.repo.PurchaseOrderRepo;
import com.example.order_service.validator.OrderRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    private final PurchaseOrderRepo repo;
    private final ProductRepo productRepo;
    private final Sinks.Many<OrderEvent> sink = Sinks.many().unicast().onBackpressureBuffer();

    public Flux<OrderEvent> events(){
        return sink.asFlux();
    }


    public Mono<OrderDTO.Response> placeOrder(Mono<OrderDTO.Request> request) {
        return request.transform(OrderRequestValidator.validate())
                .zipWhen(req -> productRepo.findByProductId(req.productId()), OrderMapper.toEntity())
                .flatMap(repo::save)
                .map(OrderMapper.toDto())
                .doOnNext(dto -> sink.tryEmitNext(OrderMapper.toCreatedOrderEvent().apply(dto)));
    }


}
