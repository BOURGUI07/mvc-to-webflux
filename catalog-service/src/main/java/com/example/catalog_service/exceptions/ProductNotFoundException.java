package com.example.catalog_service.exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String code) {
        super("Product with code " + code + " not found");
    }
}
