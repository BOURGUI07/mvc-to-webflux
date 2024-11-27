package com.example.order_service.publisher;


import com.example.order_service.dto.OrderDTO;
import com.example.order_service.enums.OrderStatus;
import com.example.order_service.events.OrderEvent;
import com.example.order_service.listener.OrderEventListener;
import com.example.order_service.mapper.OrderMapper;
import com.example.order_service.mapper.OutboxMapper;
import com.example.order_service.outbox.OrderOutboxRepo;
import com.example.order_service.outbox.OutboxDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventOutboxServiceImpl implements OrderEventListener,OrderEventOutboxService {
    private final OutboxMapper mapper;
    private final OrderOutboxRepo repo;


    @Override
    public Flux<OutboxDTO<OrderEvent>> publish() {
        return repo.findAllByOrderById()
                .map(mapper.toDTO());
    }

    @Override
    public Mono<Void> onOrderCreated(OrderDTO.Response dto) {
        return saveEvent().apply(OrderMapper.toCreatedOrderEvent().apply(dto),dto.status());
    }

    @Override
    public Mono<Void> onOrderCompleted(OrderDTO.Response dto) {
        return saveEvent().apply(OrderMapper.toCompletedOrderEvent().apply(dto),dto.status());
    }

    @Override
    public Mono<Void> onOrderCancelled(OrderDTO.Response dto) {
        return saveEvent().apply(OrderMapper.toCancelledOrderEvent().apply(dto),dto.status());
    }

    @Override
    public Mono<Void> deleteEvents(List<Long> correlationIds) {
        return repo.deleteAllById(correlationIds);
    }

    private BiFunction<OrderEvent, OrderStatus,Mono<Void>> saveEvent(){
        return (event,status) -> repo.save(mapper.toEntity().apply(event,status))
                .then(Mono.empty());
    }
}
