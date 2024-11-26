package com.example.shipping_service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static final class Exceptions{
        public static final String DUPLICATE_EVENT = "Duplicate Event";
        public static final String QUANTITY_LIMIT = "Quantity of order with id %s exceeded the limit";
    }

    public static final class BusinessLogic{
        public static final Integer QUANTITY_LIMIT = 10;
    }
}
