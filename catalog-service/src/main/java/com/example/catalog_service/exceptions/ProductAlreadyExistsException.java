package com.example.catalog_service.exceptions;

public class ProductAlreadyExistsException extends RuntimeException {
    private static final String MESSAGE = "Product with code %s already exists";

    public ProductAlreadyExistsException(String code) {
        super(String.format(MESSAGE, code));
    }
}
