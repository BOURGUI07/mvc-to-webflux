package com.example.order_service.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderNumber) {
        super("Order with order number " + orderNumber + " not found");
    }
}
