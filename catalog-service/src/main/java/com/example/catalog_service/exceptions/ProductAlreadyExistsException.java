package com.example.catalog_service.exceptions;

public class ProductAlreadyExistsException extends RuntimeException {
    /**
     *
     * @param code
     */
    public ProductAlreadyExistsException(String code) {
        super("Product with code " + code + " already exists");
    }
}
