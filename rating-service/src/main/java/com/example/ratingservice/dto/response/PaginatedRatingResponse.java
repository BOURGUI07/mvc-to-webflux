package com.example.ratingservice.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record PaginatedRatingResponse(
        List<RatingResponse> ratings, Long count, Double minRating, Double averageRating, Double maxRating) {}
