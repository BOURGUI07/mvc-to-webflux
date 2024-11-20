package com.example.catalog_service.exceptions;

public class ProductNotFoundException extends RuntimeException {
    private static final String MESSAGE= "Product with code %s not found";
    private static final String MESSAGE2= "Product with id %s not found";

    public ProductNotFoundException(String code) {
        super(String.format(MESSAGE, code));
    }

    public ProductNotFoundException(Long productId) {
        super(String.format(MESSAGE2, productId));
    }
}
