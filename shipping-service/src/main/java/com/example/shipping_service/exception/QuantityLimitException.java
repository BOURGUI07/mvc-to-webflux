package com.example.shipping_service.exception;

import java.util.UUID;

public class QuantityLimitException extends RuntimeException {
  private final static String MESSAGE = "Quantity of order with id %s exceeded the limit";

    public QuantityLimitException(UUID orderId) {
        super(String.format(MESSAGE, orderId));
    }
}
