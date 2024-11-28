package com.example.order_service.service;

import static com.example.order_service.util.Constants.Logic.*;

import com.example.order_service.cache.InventoryCacheService;
import com.example.order_service.cache.OrderCacheService;
import com.example.order_service.cache.PaymentCacheService;
import com.example.order_service.cache.ShippingCacheService;
import com.example.order_service.dto.*;
import com.example.order_service.listener.OrderEventListener;
import com.example.order_service.mapper.InventoryMapper;
import com.example.order_service.mapper.OrderMapper;
import com.example.order_service.mapper.PaymentMapper;
import com.example.order_service.mapper.ShippingMapper;
import com.example.order_service.repo.*;
import com.example.order_service.validator.OrderRequestValidator;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    private final PurchaseOrderRepo repo;
    private final ProductRepo productRepo;

    private final OrderCacheService orderCacheService;
    private final ShippingCacheService shippingCacheService;
    private final InventoryCacheService inventoryCacheService;
    private final PaymentCacheService paymentCacheService;

    private final OrderEventListener listener;

    @Transactional
    public Mono<OrderDTO.Response> placeOrder(Mono<OrderDTO.Request> request) {
        return request.transform(OrderRequestValidator.validate())
                .zipWhen(req -> productRepo.findByProductId(req.productId()), OrderMapper.toEntity())
                .flatMap(repo::save)
                .map(OrderMapper.toDto())
                .flatMap(dto -> listener.onOrderCreated(dto)
                        .thenReturn(dto)); // we didn't use doOnNext() since we wanna make this transactionnally
    }

    public Mono<OrderDetails> getOrderDetails(UUID orderId) {
        return Mono.zip(
                        orderCacheService.findById(orderId).map(OrderMapper.toDto()),
                        shippingCacheService
                                .findById(orderId)
                                .map(ShippingMapper.toDTO())
                                .defaultIfEmpty(DEFAULT_SHIPPING),
                        inventoryCacheService
                                .findById(orderId)
                                .map(InventoryMapper.toDTO())
                                .defaultIfEmpty(DEFAULT_INVENTORY),
                        paymentCacheService
                                .findById(orderId)
                                .map(PaymentMapper.toDTO())
                                .defaultIfEmpty(DEFAULT_PAYMENT))
                .map(x -> OrderMapper.toOrderDetails(x.getT1(), x.getT2(), x.getT3(), x.getT4()));
    }

    public Flux<OrderDTO.Response> findAll() {
        return orderCacheService.findAll().map(OrderMapper.toDto());
    }
}
