package com.example.ratingservice.util;

import com.example.ratingservice.events.OrderEvent;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.kafka.receiver.ReceiverOffset;

import java.util.function.Function;

public class MessageConverter {

    public static <T> Function<Message<T>, Record<T>> toRecord() {

        return message -> Record.<T>builder().message(message.getPayload())
                .key(message.getHeaders().get(KafkaHeaders.RECEIVED_KEY, String.class))
                .receiverOffset(message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, ReceiverOffset.class))
                .build();

    }



    public static <T extends OrderEvent> Function<T,Message<T>> toMessage() {
        return event -> MessageBuilder.withPayload(event)
                .setHeader(KafkaHeaders.KEY,event.orderId().toString())
                .build();

    }

}
