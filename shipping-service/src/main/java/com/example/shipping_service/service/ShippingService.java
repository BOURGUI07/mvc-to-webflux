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

    /**
     * receive the request
     * make sure the order hasn't bee processed before
     * of so raise duplicate event exception
     * also make sure the request quantity doesn't exceed the quantity limit
     * if s raise quantityLimit exception
     * if all went well, convert the request into entity
     * set the entity status to PENDING
     * save into the DB
     * convert the saved entity into DTO
     */

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


    /**
     * Receive the CancelledOrderEvent orderId
     * to cancel the shipping, it has to have PENDING status before
     * find the entity, set its status to CANCELLED
     * then save into the DB
     * finally convert the saved entity into dto
     */
    public Function<UUID, Mono<ShippingDTO>> cancelShipping() {
        return orderId -> repo.findByOrderIdAndStatus(orderId,ShippingStatus.PENDING)
                .flatMap(entity -> repo.save(entity.setStatus(ShippingStatus.CANCELLED)))
                .map(Mapper.toDto());
    }


    /**
     * Receive the CancelledOrderEvent orderId
     * to schedule the shipping, it has to have PENDING status before
     * find the entity, set its status to SCHEDULED and set delivery date
     * then save into the DB
     * finally convert the saved entity into dto
     */
    public Function<UUID, Mono<ShippingDTO>> scheduleShipping() {
        return orderId -> repo.findByOrderIdAndStatus(orderId,ShippingStatus.PENDING)
                .flatMap(e -> repo.save(e.setStatus(ShippingStatus.SCHEDULED).setDeliveryDate(Instant.now().plus(Duration.ofDays(3)))))
                .map(Mapper.toDto());
    }


}
