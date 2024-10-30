package com.example.catalog_service.mapper;


import com.example.catalog_service.domain.Product;
import com.example.catalog_service.dto.PagedResult;
import com.example.catalog_service.dto.ProductCreationRequest;
import com.example.catalog_service.dto.ProductCreationResponse;

import java.util.List;

public class Mapper {
    public static Product toEntity(ProductCreationRequest request) {
        return Product.builder()
                .code(request.code())
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .imageUrl(request.imageUrl())
                .build();
    }

    public static ProductCreationResponse toDto(Product entity) {
        return ProductCreationResponse.builder()
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .id(entity.getId())
                .build();
    }

    public static PagedResult<ProductCreationResponse> toPagedResult(List<ProductCreationResponse> data, long count, int page, int size){
        int totalPages = (int)Math.ceil((double)count / size);
        var isFirst = page==0;
        var isLast = page==totalPages-1;
        return PagedResult.<ProductCreationResponse>builder()
                .data(data)
                .totalPages(totalPages)
                .totalElements(count)
                .hasNext(!isLast)
                .hasPrevious(!isFirst)
                .isFirst(isFirst)
                .isLast(isLast)
                .pageNumber(page+1)
                .build();
    }
}
