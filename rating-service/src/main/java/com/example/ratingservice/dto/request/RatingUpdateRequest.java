package com.example.ratingservice.dto.request;

import lombok.Builder;

@Builder
public record RatingUpdateRequest(Double value, String title, String content) {}
