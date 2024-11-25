package com.example.order_service.service;

import com.example.order_service.dto.*;
import com.example.order_service.entity.PurchaseOrder;
import com.example.order_service.events.OrderEvent;
import com.example.order_service.mapper.InventoryMapper;
import com.example.order_service.mapper.OrderMapper;
import com.example.order_service.mapper.PaymentMapper;
import com.example.order_service.mapper.ShippingMapper;
import com.example.order_service.repo.*;
import com.example.order_service.validator.OrderRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    private final PurchaseOrderRepo repo;
    private final ProductRepo productRepo;
    private final InventoryRepo inventoryRepo;
    private final PaymentRepo paymentRepo;
    private final ShippingRepo shippingRepo;

    private static final OrderPaymentDTO DEFAULT_PAYMENT = OrderPaymentDTO.builder().build();
    private static final OrderInventoryDTO DEFAULT_INVENTORY = OrderInventoryDTO.builder().build();
    private static final OrderShippingDTO DEFAULT_SHIPPING = OrderShippingDTO.builder().build();

    private final Sinks.Many<OrderEvent> sink = Sinks.many().unicast().onBackpressureBuffer();

    public Flux<OrderEvent> events(){
        return sink.asFlux();
    }


    @Transactional
    public Mono<OrderDTO.Response> placeOrder(Mono<OrderDTO.Request> request) {
        return request.transform(OrderRequestValidator.validate())
                .zipWhen(req -> productRepo.findByProductId(req.productId()), OrderMapper.toEntity())
                .flatMap(repo::save)
                .map(OrderMapper.toDto())
                .doOnNext(dto -> sink.tryEmitNext(OrderMapper.toCreatedOrderEvent().apply(dto)));
    }


    public Mono<OrderDetails> getOrderDetails(UUID orderId) {
        return Mono.zip(
                repo.findByOrderId(orderId).map(OrderMapper.toDto()),
                shippingRepo.findByOrderId(orderId).map(ShippingMapper.toDTO()).defaultIfEmpty(DEFAULT_SHIPPING),
                inventoryRepo.findByOrderId(orderId).map(InventoryMapper.toDTO()).defaultIfEmpty(DEFAULT_INVENTORY),
                paymentRepo.findByOrderId(orderId).map(PaymentMapper.toDTO()).defaultIfEmpty(DEFAULT_PAYMENT)
        )
                .map(x -> OrderMapper.toOrderDetails(x.getT1(),x.getT2(),x.getT3(),x.getT4()));
    }




}
