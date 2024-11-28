package com.example.order_service.service;

import com.example.order_service.cache.InventoryCacheService;
import com.example.order_service.cache.OrderCacheService;
import com.example.order_service.cache.PaymentCacheService;
import com.example.order_service.cache.ShippingCacheService;
import com.example.order_service.entity.OrderInventory;
import com.example.order_service.entity.OrderPayment;
import com.example.order_service.entity.OrderShipping;
import com.example.order_service.entity.PurchaseOrder;
import com.example.order_service.enums.OrderStatus;
import com.example.order_service.listener.OrderEventListener;
import com.example.order_service.mapper.OrderMapper;
import com.example.order_service.repo.PurchaseOrderRepo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderFulfillmentService {

    private final PurchaseOrderRepo repo;

    private final OrderCacheService orderCacheService;
    private final ShippingCacheService shippingCacheService;
    private final InventoryCacheService inventoryCacheService;
    private final PaymentCacheService paymentCacheService;

    private final OrderEventListener listener;

    public Mono<Void> completeOrder(UUID orderId) {
        return Mono.zip(
                        repo.findByOrderIdAndStatus(orderId, OrderStatus.CREATED),
                        paymentCacheService.findById(orderId).map(OrderPayment::getSuccess),
                        shippingCacheService.findById(orderId).map(OrderShipping::getSuccess),
                        inventoryCacheService.findById(orderId).map(OrderInventory::getSuccess))
                .flatMap(x -> {
                    var order = x.getT1();

                    var isPaymentSuccessful = x.getT2();
                    var isShippingSuccessful = x.getT3();
                    var isInventorySuccessful = x.getT4();

                    return Mono.fromSupplier(() -> order)
                            .filter(__ -> isPaymentSuccessful && isShippingSuccessful && isInventorySuccessful);
                })
                .cast(PurchaseOrder.class)
                .flatMap(order -> repo.save(order.setStatus(OrderStatus.COMPLETED))
                        .then(orderCacheService.doOnChanged(order))
                        .thenReturn(order))
                .retryWhen(Retry.max(1).filter(OptimisticLockingFailureException.class::isInstance))
                .map(OrderMapper.toDto())
                .flatMap(listener::onOrderCompleted);
    }

    public Mono<Void> cancelOrder(UUID orderId) {
        return repo.findByOrderIdAndStatus(orderId, OrderStatus.CREATED)
                .flatMap(order -> repo.save(order.setStatus(OrderStatus.CANCELLED))
                        .then(orderCacheService.doOnChanged(order))
                        .thenReturn(order))
                .retryWhen(Retry.max(1).filter(OptimisticLockingFailureException.class::isInstance))
                .map(OrderMapper.toDto())
                .flatMap(listener::onOrderCancelled);
    }
}
