package com.example.order_service.service;

import com.example.order_service.cache.ShippingCacheService;
import com.example.order_service.dto.OrderShippingDTO;
import com.example.order_service.entity.OrderShipping;
import com.example.order_service.mapper.ShippingMapper;
import com.example.order_service.repo.ShippingRepo;
import java.util.function.BiFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class ShippingService {
    private final ShippingCacheService shippingCacheService;

    private final ShippingRepo repo;
    private final OrderFulfillmentService service;

    public Mono<Void> handleReadyShipping(OrderShippingDTO dto) {
        return save().apply(dto, true).then(service.completeOrder(dto.orderId()));
    }

    public Mono<Void> handleFailedShipping(OrderShippingDTO dto) {
        return save().apply(dto, false).then(service.cancelOrder(dto.orderId()));
    }

    public Mono<Void> handleCancelledShipping(OrderShippingDTO dto) {
        return repo.findByOrderId(dto.orderId())
                .flatMap(shipping ->
                        repo.save(shipping.setStatus(dto.status())).then(shippingCacheService.doOnChanged(shipping)))
                .then();
    }

    public Mono<Void> handleScheduledShipping(OrderShippingDTO dto) {
        return repo.findByOrderId(dto.orderId())
                .flatMap(shipping -> repo.save(shipping.setStatus(dto.status()).setDeliveryDate(dto.deliveryDate())))
                .then();
    }

    private BiFunction<OrderShippingDTO, Boolean, Mono<OrderShipping>> save() {
        return (dto, success) -> repo.findByOrderId(dto.orderId())
                .defaultIfEmpty(ShippingMapper.toEntity().apply(dto))
                .flatMap(shipping -> repo.save(shipping.setSuccess(success))
                        .then(shippingCacheService.doOnChanged(shipping))
                        .thenReturn(shipping));
    }
}
