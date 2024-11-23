package com.example.notification_service.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "notification.service")
public record NotificationServiceProperties(
        @DefaultValue(value = "younessbourgui07@gmail.com")
        String supportEmail
) {
}
