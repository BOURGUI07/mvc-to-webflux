package com.example.notification_service.config;

import com.example.notification_service.events.CustomerEventConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CustomerEventConsumerConfig {
    private final CustomerEventConsumer customerEventConsumer;
}
