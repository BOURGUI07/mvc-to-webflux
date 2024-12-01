package com.example.ratingservice.dto;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.net.URI;

@Builder
@ConfigurationProperties(prefix = "rating.service")
public record RatingServiceProperties(
        @DefaultValue("10")
        int defaultPageSize,

        @DefaultValue("https://api.bookstore.com/errors/not-found")
        URI exceptionNotFound,

        @DefaultValue("https://api.bookstore.com/errors/general")
        URI exceptionGeneral,

        @DefaultValue("https://api.bookstore.com/errors/bad-request")
        URI exceptionBadRequest,

        @DefaultValue("rating-service")
        String name
) {

}
