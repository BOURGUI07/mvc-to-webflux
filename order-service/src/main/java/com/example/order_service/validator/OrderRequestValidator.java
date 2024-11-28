package com.example.order_service.validator;

import com.example.order_service.dto.OrderDTO;
import com.example.order_service.exception.ApplicationExceptions;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import reactor.core.publisher.Mono;

public class OrderRequestValidator {

    public static Predicate<OrderDTO.Request> hasCustomerId() {
        return dto -> Objects.nonNull(dto.customerId());
    }

    public static Predicate<OrderDTO.Request> hasProductId() {
        return dto -> Objects.nonNull(dto.productId());
    }

    public static Predicate<OrderDTO.Request> hasValidQty() {
        return dto -> Objects.nonNull(dto.quantity()) && dto.quantity() > 0;
    }

    public static UnaryOperator<Mono<OrderDTO.Request>> validate() {
        return mono -> mono.filter(hasCustomerId())
                .switchIfEmpty(ApplicationExceptions.invalidRequest("Customer Id is Required"))
                .filter(hasProductId())
                .switchIfEmpty(ApplicationExceptions.invalidRequest("Product Id is Required"))
                .filter(hasValidQty())
                .switchIfEmpty(ApplicationExceptions.invalidRequest("Quantity is Required and Should be Positive"));
    }
}
