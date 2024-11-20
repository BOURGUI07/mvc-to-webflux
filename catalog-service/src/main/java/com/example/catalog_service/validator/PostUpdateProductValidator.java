package com.example.catalog_service.validator;

import com.example.catalog_service.domain.Product;
import com.example.catalog_service.dto.ProductCreationRequest;
import com.example.catalog_service.exceptions.ApplicationsExceptions;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class PostUpdateProductValidator {

    public static UnaryOperator<Mono<Product>> validate(){
        return product -> product
                .filter(hasValidQuantity())
                .switchIfEmpty(ApplicationsExceptions.invalidRequest("Product Quantity is Invalid"))
                .filter(hasValidPrice())
                .switchIfEmpty(ApplicationsExceptions.invalidRequest("Product Price is Invalid"));
    }

    public static Predicate<Product> hasValidPrice() {
        return product ->product.getPrice().doubleValue() > 0;
    }

    public static Predicate<Product> hasValidQuantity() {
        return product -> product.getAvailableQuantity()> 0;
    }
}
