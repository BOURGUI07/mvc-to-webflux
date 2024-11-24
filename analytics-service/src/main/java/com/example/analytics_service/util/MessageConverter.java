package com.example.analytics_service.util;

import com.example.analytics_service.events.ProductEvent;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import reactor.kafka.receiver.ReceiverOffset;

import java.util.function.Function;

public class MessageConverter {


    public static <T extends ProductEvent> Function<Message<T>,Record<T>> toRecord(){
        return msg -> Record.<T>builder()
                .message(msg.getPayload())
                .key(msg.getHeaders().get(KafkaHeaders.RECEIVED_KEY, String.class))
                .receiverOffset(msg.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, ReceiverOffset.class))
                .build();
    }
}
