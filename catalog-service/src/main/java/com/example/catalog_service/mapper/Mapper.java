package com.example.catalog_service.mapper;

import com.example.catalog_service.domain.Product;
import com.example.catalog_service.dto.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Mapper {
    public static Function<ProductCreationRequest,Product> toEntity( ) {
        return request -> Product.builder()
                .code(request.code())
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .imageUrl(request.imageUrl())
                .build();
    }

    public static Function<Product, ProductResponse> toDto() {
        return entity -> ProductResponse.builder()
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .id(entity.getId())
                .build();
    }

    public static PagedResult<ProductResponse> toPagedResult(
            List<ProductResponse> data, long count, int page, int size) {
        int totalPages = (int) Math.ceil((double) count / size);
        var isFirst = page == 0;
        var isLast = page == totalPages - 1;
        return PagedResult.<ProductResponse>builder()
                .data(data)
                .totalPages(totalPages)
                .totalElements(count)
                .hasNext(!isLast)
                .hasPrevious(!isFirst)
                .isFirst(isFirst)
                .isLast(isLast)
                .pageNumber(page + 1)
                .build();
    }

}
