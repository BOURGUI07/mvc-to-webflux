package com.example.customer_service.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PageResult<T>(
        List<T> data,
        long totalElements,
        int pageNumber,
        int totalPages,
        boolean isFirst,
        boolean isLast,
        boolean hasNext,
        boolean hasPrevious
) {
}
