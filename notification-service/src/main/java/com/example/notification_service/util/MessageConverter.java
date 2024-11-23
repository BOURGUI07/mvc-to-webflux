package com.example.notification_service.util;

import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import reactor.kafka.receiver.ReceiverOffset;

import java.util.function.Function;

public class MessageConverter {
    public static <T> Function<Message<T>, Record<T>> toRecord() {

        return message -> Record.<T>builder().message(message.getPayload())
                .key(message.getHeaders().get(KafkaHeaders.RECEIVED_KEY, String.class))
                .receiverOffset(message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, ReceiverOffset.class))
                .build();

    }

}
