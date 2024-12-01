package com.example.ratingservice.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PaginatedRatingResponse(
        List<RatingResponse> ratings,
        Long count,
        Double minRating,
        Double averageRating,
        Double maxRating
) {
}
