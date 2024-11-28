package com.example.order_service.listener;

import com.example.order_service.dto.OrderDTO;
import reactor.core.publisher.Mono;

public interface OrderEventListener {

    Mono<Void> onOrderCreated(OrderDTO.Response dto);

    Mono<Void> onOrderCompleted(OrderDTO.Response dto);

    Mono<Void> onOrderCancelled(OrderDTO.Response dto);
}
