package com.example.order_service.service;

import com.example.order_service.dto.OrderDTO;
import com.example.order_service.entity.OrderInventory;
import com.example.order_service.entity.OrderPayment;
import com.example.order_service.entity.OrderShipping;
import com.example.order_service.enums.OrderStatus;
import com.example.order_service.mapper.OrderMapper;
import com.example.order_service.repo.InventoryRepo;
import com.example.order_service.repo.PaymentRepo;
import com.example.order_service.repo.PurchaseOrderRepo;
import com.example.order_service.repo.ShippingRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderFulfillmentService {

    private final PurchaseOrderRepo repo;
    private final PaymentRepo paymentRepo;
    private final InventoryRepo inventoryRepo;
    private final ShippingRepo shippingRepo;

    public Mono<OrderDTO.Response> completeOrder(UUID orderId) {
        return Mono.zip(
                repo.findByOrderIdAndStatus(orderId, OrderStatus.CREATED),
                paymentRepo.findByOrderId(orderId).map(OrderPayment::getSuccess),
                shippingRepo.findByOrderId(orderId).map(OrderShipping::getSuccess),
                inventoryRepo.findByOrderId(orderId).map(OrderInventory::getSuccess)
        )
                .flatMap(x -> {
                    var order = x.getT1();

                    var isPaymentSuccessful = x.getT2();
                    var isShippingSuccessful = x.getT3();
                    var isInventorySuccessful = x.getT4();

                    return Mono.fromSupplier(() -> order)
                            .filter(__-> isPaymentSuccessful && isShippingSuccessful && isInventorySuccessful);
                })
                .flatMap(order -> repo.save(order.setStatus(OrderStatus.COMPLETED)))
                .retryWhen(Retry.max(1).filter(OptimisticLockingFailureException.class::isInstance))
                .map(OrderMapper.toDto());

    }

    public Mono<OrderDTO.Response> cancelOrder(UUID orderId) {
        return repo.findByOrderIdAndStatus(orderId,OrderStatus.CREATED)
                .flatMap(order -> repo.save(order.setStatus(OrderStatus.CANCELLED)))
                .retryWhen(Retry.max(1).filter(OptimisticLockingFailureException.class::isInstance))
                .map(OrderMapper.toDto());
    }
}
