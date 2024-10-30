package com.example.catalog_service.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record PagedResult<T>(
        List<T> data,
        long totalElements,
        int pageNumber,
        int totalPages,
        boolean isFirst,
        boolean isLast,
        boolean hasNext,
        boolean hasPrevious) {}
