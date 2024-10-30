package com.example.catalog_service.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PagedResult<T>(
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
