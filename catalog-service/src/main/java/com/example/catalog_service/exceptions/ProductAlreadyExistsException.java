package com.example.catalog_service.exceptions;

public class ProductAlreadyExistsException extends RuntimeException {

    public ProductAlreadyExistsException(String code) {
        super("Product with code " + code + " already exists");
    }
}
