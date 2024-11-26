package com.example.shipping_service.exception;

import com.example.shipping_service.util.Constants;

import java.util.UUID;

public class QuantityLimitException extends RuntimeException {

    public QuantityLimitException(UUID orderId) {
        super(String.format(Constants.Exceptions.QUANTITY_LIMIT, orderId));
    }
}
