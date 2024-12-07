package com.example.customer_service.validator;

import com.example.customer_service.dto.CustomerDTO;
import com.example.customer_service.exceptions.ApplicationExceptions;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * The customer creation request has to have ALL fields as non-null
 * valid email
 * valid balance
 */

public class CustomerRequestValidator {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";


        public static UnaryOperator<Mono<CustomerDTO.Request>> validate(){
            return request -> request
                    .filter(hasValidBalance())
                    .switchIfEmpty(ApplicationExceptions.invalidRequest("Balance is Required and Must be Positive"))
                    .filter(hasValidEmail())
                    .switchIfEmpty(ApplicationExceptions.invalidRequest("Email is Required and Must be Valid"))
                    .filter(hasCity())
                    .switchIfEmpty(ApplicationExceptions.invalidRequest("City is Required"))
                    .filter(hasCountry())
                    .switchIfEmpty(ApplicationExceptions.invalidRequest("Country is Required"))
                    .filter(hasState())
                    .switchIfEmpty(ApplicationExceptions.invalidRequest("State is Required"))
                    .filter(hasStreet())
                    .switchIfEmpty(ApplicationExceptions.invalidRequest("Street is Required"))
                    .filter(hasUsername())
                    .switchIfEmpty(ApplicationExceptions.invalidRequest("Username is Required"));
        }

        public static Predicate<CustomerDTO.Request> hasUsername(){
            return dto -> Objects.nonNull(dto.username());
        }

        public static Predicate<CustomerDTO.Request> hasValidBalance(){
            return dto -> Objects.nonNull(dto.balance()) && dto.balance().doubleValue() > 0.0;
        }

        public static Predicate<CustomerDTO.Request> hasValidEmail(){
            return dto -> Objects.nonNull(dto.email()) && dto.email().matches(EMAIL_REGEX);
        }

        public static Predicate<CustomerDTO.Request> hasCity(){
            return dto -> Objects.nonNull(dto.city());
        }

        public static Predicate<CustomerDTO.Request> hasCountry(){
            return dto -> Objects.nonNull(dto.country());
        }

        public static Predicate<CustomerDTO.Request> hasStreet(){
            return dto -> Objects.nonNull(dto.street());
        }

        public static Predicate<CustomerDTO.Request> hasState(){
            return dto -> Objects.nonNull(dto.state());
        }
}
