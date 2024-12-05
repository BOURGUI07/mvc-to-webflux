package com.example.catalog_service.events;

import java.util.UUID;

/**
 * All events that react to orders, gonna have orderId field
 */

public interface OrderSaga {
    UUID orderId();
}
