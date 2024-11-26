package com.example.catalog_service.exceptions;

import com.example.catalog_service.util.Constants;

import java.util.UUID;

import static com.example.catalog_service.util.Constants.Exceptions.DUPLICATE_EVENT;

public class DuplicatedEventException extends RuntimeException {


    public DuplicatedEventException(UUID orderId) {
        super(String.format(DUPLICATE_EVENT, orderId));
    }
}
