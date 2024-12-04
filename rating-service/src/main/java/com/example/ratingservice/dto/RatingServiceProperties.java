package com.example.ratingservice.dto;

import java.net.URI;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Builder
@ConfigurationProperties(prefix = "rating.service")
public record RatingServiceProperties(
        @DefaultValue("10") int defaultPageSize,
        @DefaultValue("https://api.bookstore.com/errors/not-found") URI exceptionNotFound,
        @DefaultValue("https://api.bookstore.com/errors/general") URI exceptionGeneral,
        @DefaultValue("https://api.bookstore.com/errors/bad-request") URI exceptionBadRequest,
        @DefaultValue("rating-service") String name) {}
