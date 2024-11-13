package com.example.catalog_service.validator;

import com.example.catalog_service.dto.ProductCreationRequest;
import com.example.catalog_service.dto.ProductUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.function.Predicate;

@Service
public class UpdateRequestValidator extends RequestValidator<ProductUpdateRequest> {
    @Override
    public Predicate<ProductUpdateRequest> hasName() {
        return dto -> Objects.nonNull(dto.name());
    }

    @Override
    public Predicate<ProductUpdateRequest> hasValidPrice() {
        return dto -> Objects.nonNull(dto.price()) && dto.price().doubleValue() > 0;
    }

    @Override
    public Predicate<ProductUpdateRequest> hasDescription() {
        return dto -> Objects.nonNull(dto.description());
    }

    @Override
    public Predicate<ProductUpdateRequest> hasImageUrl() {
        return dto -> Objects.nonNull(dto.imageUrl());
    }
}
