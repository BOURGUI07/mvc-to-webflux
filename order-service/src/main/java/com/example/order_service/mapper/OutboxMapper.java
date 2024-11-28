package com.example.order_service.mapper;

import com.example.order_service.enums.OrderStatus;
import com.example.order_service.events.OrderEvent;
import com.example.order_service.outbox.OrderOutbox;
import com.example.order_service.outbox.OutboxDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class OutboxMapper {

    private final ObjectMapper mapper;

    public BiFunction<OrderEvent, OrderStatus, OrderOutbox> toEntity() {
        return (orderEvent, status) -> OrderOutbox.builder()
                .status(status)
                .message(toBytes().apply(orderEvent))
                .build();
    }

    private Function<OrderEvent, byte[]> toBytes() {
        return event -> {
            try {
                return mapper.writeValueAsBytes(event);
            } catch (JsonProcessingException e) {
                log.warn("Failed to convert order event to bytes: {}", e.getMessage());
            }
            return null;
        };
    }

    public Function<OrderOutbox, OutboxDTO<OrderEvent>> toDTO() {
        return entity -> OutboxDTO.<OrderEvent>builder()
                .correlationId(entity.getId())
                .event(toEvent().apply(entity.getStatus(), entity.getMessage()))
                .build();
    }

    private <T> T toObject(byte[] bytes, Class<T> type) {
        try {
            return mapper.readValue(bytes, type);
        } catch (IOException e) {
            log.warn("Failed to convert bytes to OrderEvent: {}", e.getMessage());
        }
        return null;
    }

    private BiFunction<OrderStatus, byte[], OrderEvent> toEvent() {
        return (status, bytes) -> switch (status) {
            case CREATED -> toObject(bytes, OrderEvent.Created.class);
            case COMPLETED -> toObject(bytes, OrderEvent.Completed.class);
            case CANCELLED -> toObject(bytes, OrderEvent.Cancelled.class);
        };
    }
}
