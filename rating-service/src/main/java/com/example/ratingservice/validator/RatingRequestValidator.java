package com.example.ratingservice.validator;

import com.example.ratingservice.dto.request.RatingCreationRequest;
import com.example.ratingservice.exceptions.ApplicationExceptions;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class RatingRequestValidator {

    public static UnaryOperator<Mono<RatingCreationRequest>> validate(){
        return mono -> mono
                .filter(hasCustomerId())
                .switchIfEmpty(ApplicationExceptions.invalidRequest("Customer id is required"))
                .filter(hasOrderId())
                .switchIfEmpty(ApplicationExceptions.invalidRequest("Order id is required"))
                .filter(hasProductId())
                .switchIfEmpty(ApplicationExceptions.invalidRequest("Product id is required"))
                .filter(isValueValid())
                .switchIfEmpty(ApplicationExceptions.invalidRequest("Value is required and should be between 1 and 5"));
    }


    private static Predicate<RatingCreationRequest> isValueValid(){
        return dto -> {
            var value = dto.value();
            return Objects.nonNull(value) && value<=5 && value>=1;
        };
    }

    private static Predicate<RatingCreationRequest> hasProductId(){
        return dto -> Objects.nonNull(dto.productId());
    }

    private static Predicate<RatingCreationRequest> hasCustomerId(){
        return dto -> Objects.nonNull(dto.customerId());
    }

    private static Predicate<RatingCreationRequest> hasOrderId(){
        return dto -> Objects.nonNull(dto.orderId());
    }
}
