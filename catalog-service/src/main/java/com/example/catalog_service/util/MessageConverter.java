package com.example.catalog_service.util;

import com.example.catalog_service.events.OrderSaga;
import com.example.catalog_service.events.ProductEvent;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.kafka.receiver.ReceiverOffset;

import java.util.function.Function;

public class MessageConverter {

    /**
     * The Catalog-Service gonna received events wrapped into Message form
     * That message gonna be convert into a custom wrapper called, 'Record'
     * The message payload is the Record's message
     * Extract the key and the receiverOffset to acknowledge after consuming the payload
     */

    public static <T> Function<Message<T>,Record<T>> toRecord() {

        return message -> Record.<T>builder().message(message.getPayload())
                .key(message.getHeaders().get(KafkaHeaders.RECEIVED_KEY, String.class))
                .receiverOffset(message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, ReceiverOffset.class))
                .build();

    }


    /**
     * When we're about to send the events as a response, we gonna
     * wrap the responses into Message forms.
     */
    public static <T extends OrderSaga> Function<T,Message<T>> toMessage() {
        return event -> MessageBuilder.withPayload(event)
                .setHeader(KafkaHeaders.KEY,event.orderId().toString())
                .build();

    }

    public static <T extends ProductEvent> Function<T,Message<T>> toProductEventMessage() {
        return event -> MessageBuilder.withPayload(event)
                .setHeader(KafkaHeaders.KEY,event.code())
                .build();

    }
}
