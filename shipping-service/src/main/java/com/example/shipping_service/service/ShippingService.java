package com.example.shipping_service.service;

import com.example.shipping_service.domain.ShippingStatus;
import com.example.shipping_service.dto.ShippingDTO;
import com.example.shipping_service.exception.ApplicationExceptions;
import com.example.shipping_service.mapper.Mapper;
import com.example.shipping_service.repo.ShippingRepo;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.example.shipping_service.util.Constants.BusinessLogic.QUANTITY_LIMIT;

@RequiredArgsConstructor
@Service
@Slf4j
public class ShippingService {
    private final ShippingRepo repo;


    public Function<ShippingDTO.Request, Mono<ShippingDTO>> planShipping() {
        return request -> {
            var orderId = request.orderId();
            return repo.existsByOrderId(orderId)
                    .filter(Predicate.not(b->b))
                    .switchIfEmpty(ApplicationExceptions.duplicateEvent())
                    .thenReturn(request)
                    .filter(x -> x.quantity()<=QUANTITY_LIMIT)
                    .switchIfEmpty(ApplicationExceptions.quantityLimit(orderId))
                    .map(Mapper.toEntity())
                    .flatMap(entity -> repo.save(entity.setStatus(ShippingStatus.PENDING)))
                    .map(Mapper.toDto());
        };
    }

    public Function<UUID, Mono<ShippingDTO>> cancelShipping() {
        return orderId -> repo.findByOrderIdAndStatus(orderId,ShippingStatus.PENDING)
                .flatMap(entity -> repo.save(entity.setStatus(ShippingStatus.CANCELLED)))
                .map(Mapper.toDto());
    }

    public Function<UUID, Mono<ShippingDTO>> scheduleShipping() {
        return orderId -> repo.findByOrderIdAndStatus(orderId,ShippingStatus.PENDING)
                .flatMap(e -> repo.save(e.setStatus(ShippingStatus.SCHEDULED).setDeliveryDate(Instant.now().plus(Duration.ofDays(3)))))
                .map(Mapper.toDto());
    }


}
