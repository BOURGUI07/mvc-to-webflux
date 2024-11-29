package com.example.order_service.util;

import com.example.order_service.dto.OrderInventoryDTO;
import com.example.order_service.dto.OrderPaymentDTO;
import com.example.order_service.dto.OrderShippingDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static final class Logic {
        public static final OrderPaymentDTO DEFAULT_PAYMENT =
                OrderPaymentDTO.builder().build();
        public static final OrderInventoryDTO DEFAULT_INVENTORY =
                OrderInventoryDTO.builder().build();
        public static final OrderShippingDTO DEFAULT_SHIPPING =
                OrderShippingDTO.builder().build();
    }

    public static final class RedisKeys {
        public static final String ORDER_KEY = "orders";
        public static final String INVENTORY_KEY = "inventory";
        public static final String SHIPPING_KEY = "shipping";
        public static final String PAYMENT_KEY = "payment";
    }

    public static final class PublicApiUrls {
        public static final String ORDER_BASE_URL = "/api/orders";
    }
}
