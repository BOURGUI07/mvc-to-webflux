package com.example.ratingservice.util;

import lombok.Builder;
import reactor.kafka.receiver.ReceiverOffset;

@Builder
public record Record<T>(
        T message,
        String key,
        ReceiverOffset receiverOffset
) {
}
