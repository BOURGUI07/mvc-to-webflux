package com.example.ratingservice.exceptions;

import reactor.core.publisher.Mono;

public class ApplicationExceptions {

    public static <T> Mono<T> invalidRequest(String message) {
        return Mono.error(new InvalidRatingRequestException(message));
    }

    public static <T> Mono<T> notFound(Long ratingId) {
        return Mono.error(new RatingNotFoundException(ratingId));
    }

    public static <T> Mono<T> duplicateEvent() {
        return Mono.error(new DuplicateEventException());
    }
}
