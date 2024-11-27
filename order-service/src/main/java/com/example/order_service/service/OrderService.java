package com.example.order_service.service;

import com.example.order_service.dto.*;
import com.example.order_service.entity.PurchaseOrder;
import com.example.order_service.events.OrderEvent;
import com.example.order_service.listener.OrderEventListener;
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

import static com.example.order_service.util.Constants.Logic.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    private final PurchaseOrderRepo repo;
    private final ProductRepo productRepo;
    private final InventoryRepo inventoryRepo;
    private final PaymentRepo paymentRepo;
    private final ShippingRepo shippingRepo;
    private final OrderEventListener listener;


    @Transactional
    public Mono<OrderDTO.Response> placeOrder(Mono<OrderDTO.Request> request) {
        return request.transform(OrderRequestValidator.validate())
                .zipWhen(req -> productRepo.findByProductId(req.productId()), OrderMapper.toEntity())
                .flatMap(repo::save)
                .map(OrderMapper.toDto())
                .flatMap(dto -> listener.onOrderCreated(dto).thenReturn(dto)); //we didn't use doOnNext() since we wanna make this transactionnally
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


    public Flux<OrderDTO.Response> findAll() {
        return repo.findAll().map(OrderMapper.toDto());
    }




}
