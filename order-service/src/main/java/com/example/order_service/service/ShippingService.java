package com.example.order_service.service;

import com.example.order_service.dto.OrderPaymentDTO;
import com.example.order_service.dto.OrderShippingDTO;
import com.example.order_service.entity.OrderPayment;
import com.example.order_service.entity.OrderShipping;
import com.example.order_service.mapper.PaymentMapper;
import com.example.order_service.mapper.ShippingMapper;
import com.example.order_service.repo.PaymentRepo;
import com.example.order_service.repo.ShippingRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class ShippingService {
    private final ShippingRepo repo;
    private final OrderFulfillmentService service;

    public Mono<Void> handleReadyShipping(OrderShippingDTO dto){
        return save().apply(dto,true)
                .then(service.completeOrder(dto.orderId()));
    }

    public Mono<Void> handleFailedShipping(OrderShippingDTO dto){
        return save().apply(dto,false)
                .then(service.cancelOrder(dto.orderId()));
    }

    public Mono<Void> handleCancelledShipping(OrderShippingDTO dto){
        return repo.findByOrderId(dto.orderId())
                .flatMap(shipping -> repo.save(shipping.setStatus(dto.status())))
                .then();
    }

    public Mono<Void> handleScheduledShipping(OrderShippingDTO dto){
        return repo.findByOrderId(dto.orderId())
                .flatMap(shipping -> repo.save(shipping.setStatus(dto.status()).setDeliveryDate(dto.deliveryDate())))
                .then();
    }

    private BiFunction<OrderShippingDTO, Boolean, Mono<OrderShipping>> save(){
        return (dto, success) -> repo.findByOrderId(dto.orderId())
                .defaultIfEmpty(ShippingMapper.toEntity().apply(dto))
                .flatMap(shipping -> repo.save(shipping.setSuccess(success)));
    }

}
