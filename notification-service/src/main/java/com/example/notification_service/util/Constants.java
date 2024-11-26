package com.example.notification_service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static final class Exceptions{
        public static final String DUPLICATE_EVENT = "Duplicate Event Exception";
        public static final String EMAIL_FAILURE = "Failed to send email";
    }
}
