package com.example.analytics_service.mapper;

import com.example.analytics_service.dto.ProductViewDTO;
import com.example.analytics_service.entity.ProductView;

import java.util.function.Function;

public class Mapper {


    public static Function<ProductView, ProductViewDTO> toDTO(){
        return entity -> ProductViewDTO.builder()
                .productCode(entity.getProductCode())
                .viewCount(entity.getViewCount())
                .createdAt(entity.getCreatedAt())
                .updateAt(entity.getUpdatedAt())
                .build();
    }
}
