package com.example.catalog_service.listener;

import com.example.catalog_service.dto.ProductActionDTO;
import com.example.catalog_service.events.ProductEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductEventListener {

    Flux<ProductEvent> productEvents();

    default void listen(ProductActionDTO dto){
        switch (dto.action()) {
            case VIEWED -> handleViewedProduct(dto);
            case CREATED -> handleCreatedProduct(dto);
            case UPDATED -> handleUpdatedProduct(dto);
            case DELETED -> handleDeletedProduct(dto);
        }
    }

    void handleDeletedProduct(ProductActionDTO dto);

    void handleUpdatedProduct(ProductActionDTO dto);

    void handleCreatedProduct(ProductActionDTO dto);

    void handleViewedProduct(ProductActionDTO dto);
}
