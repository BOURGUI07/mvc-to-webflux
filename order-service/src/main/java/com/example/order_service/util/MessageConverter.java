package com.example.order_service.util;

import com.example.order_service.events.OrderEvent;
import com.example.order_service.events.OrderSaga;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.kafka.receiver.ReceiverOffset;

import java.util.function.Function;

public class MessageConverter {

    private static final String DESTINATION_HEADER = "spring.cloud.stream.sendto.destination";
    private static final String ORDER_EVENT_CHANNEL = "order-events-channel";


    public static <T> Function<Message<T>, Record<T>> toRecord() {
        return message -> Record.<T>builder().message(message.getPayload())
                .key(message.getHeaders().get(KafkaHeaders.RECEIVED_KEY, String.class))
                .receiverOffset(message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, ReceiverOffset.class))
                .build();

    }



    public static <T extends OrderEvent> Function<T,Message<T>> toMessage() {
        return event -> MessageBuilder.withPayload(event)
                .setHeader(KafkaHeaders.KEY,event.orderId().toString())
                .setHeader(DESTINATION_HEADER, ORDER_EVENT_CHANNEL)
                .build();

    }
}
