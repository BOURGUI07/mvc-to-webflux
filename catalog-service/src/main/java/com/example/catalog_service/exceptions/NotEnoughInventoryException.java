package com.example.catalog_service.exceptions;

import com.example.catalog_service.util.Constants;

import static com.example.catalog_service.util.Constants.Exceptions.NOT_ENOUGH_INVENTORY;

public class NotEnoughInventoryException extends RuntimeException {

    public NotEnoughInventoryException(Long productId) {
        super(String.format(NOT_ENOUGH_INVENTORY, productId));
    }
}
