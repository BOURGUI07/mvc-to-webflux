package com.example.catalog_service.exceptions;

public class NotEnoughInventoryException extends RuntimeException {

    private static final String MESSAGE = "Product with id %s doesn't have enough inventory";

    public NotEnoughInventoryException(Long productId) {
        super(String.format(MESSAGE, productId));
    }
}
