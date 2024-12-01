package com.example.ratingservice.mapper;

import com.example.ratingservice.dto.request.RatingCreationRequest;
import com.example.ratingservice.dto.response.RatingResponse;
import com.example.ratingservice.entity.Rating;

import java.util.Optional;
import java.util.function.Function;

public class RatingMapper {
    public static Function<RatingCreationRequest, Rating> toEntity(){
        return dto -> {
            var rating = Rating.builder()
                    .customerId(dto.customerId())
                    .value(dto.value())
                    .productId(dto.productId())
                    .orderId(dto.orderId())
                    .build();
            Optional.ofNullable(dto.content()).ifPresent(rating::setContent);
            Optional.ofNullable(dto.title()).ifPresent(rating::setTitle);
            return rating;
        };
    }

    public static Function<Rating, RatingResponse> toDto(){
        return entity -> RatingResponse.builder()
                .customerId(entity.getCustomerId())
                .value(entity.getValue())
                .productId(entity.getProductId())
                .orderId(entity.getOrderId())
                .content(entity.getContent())
                .title(entity.getTitle())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
