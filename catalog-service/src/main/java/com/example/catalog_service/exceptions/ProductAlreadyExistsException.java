package com.example.catalog_service.exceptions;

import com.example.catalog_service.util.Constants;

import static com.example.catalog_service.util.Constants.Exceptions.ALREADY_EXISTS;

public class ProductAlreadyExistsException extends RuntimeException {

    public ProductAlreadyExistsException(String code) {
        super(String.format(ALREADY_EXISTS, code));
    }
}
