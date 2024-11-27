package com.example.order_service.service;

import com.example.order_service.dto.OrderInventoryDTO;
import com.example.order_service.dto.OrderPaymentDTO;
import com.example.order_service.entity.OrderInventory;
import com.example.order_service.entity.OrderPayment;
import com.example.order_service.mapper.InventoryMapper;
import com.example.order_service.mapper.PaymentMapper;
import com.example.order_service.repo.PaymentRepo;
import com.example.order_service.service.cache.PaymentCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class PaymentService {
    private final PaymentCacheService paymentCacheService;

    private final PaymentRepo repo;
    private final OrderFulfillmentService service;


    public Mono<Void> handleSuccessfulPayment(OrderPaymentDTO dto){
        return save().apply(dto,true)
                .then(service.completeOrder(dto.orderId()));
    }

    public Mono<Void> handleFailedPayment(OrderPaymentDTO dto){
        return save().apply(dto,false)
                .then(service.cancelOrder(dto.orderId()));
    }

    public Mono<Void> handleRolledBackPayment(OrderPaymentDTO dto){
        return repo.findByOrderId(dto.orderId())
                .flatMap(payment -> repo.save(payment.setStatus(dto.status())).then(paymentCacheService.doOnChanged(payment)))
                .then();
    }

    private BiFunction<OrderPaymentDTO, Boolean, Mono<OrderPayment>> save(){
        return (dto, success) -> repo.findByOrderId(dto.orderId())
                .defaultIfEmpty(PaymentMapper.toEntity().apply(dto))
                .flatMap(payment -> repo.save(payment.setSuccess(success)).then(paymentCacheService.doOnChanged(payment)).thenReturn(payment));
    }


}
