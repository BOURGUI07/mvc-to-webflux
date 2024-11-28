package com.example.catalog_service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static final class Exceptions{
        public static final String DUPLICATE_EVENT = "Duplicated Event With OrderId: %s";
        public static final String NOT_FOUND_CODE = "Product with code %s not found";
        public static final String NOT_FOUND_ID = "Product with id %s not found";
        public static final String NOT_ENOUGH_INVENTORY = "Product with id %s doesn't have enough inventory";
        public static final String ALREADY_EXISTS ="Product with code %s already exists";
    }

    public static final class RedisKeys{
        public static final String PRODUCT_KEY = "products";
    }



}
