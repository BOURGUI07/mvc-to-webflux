package com.example.ratingservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static final class Exceptions {
        public static final String DUPLICATE_EVENT = "Duplicate event";
        public static final String NOT_FOUND = "Rating with id %s not found";
    }

    public static final class ApiBaseUrl {
        public static final String RATING_BASE_URL = "/api/ratings";
    }
}
