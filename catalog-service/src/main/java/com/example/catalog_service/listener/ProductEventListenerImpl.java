package com.example.catalog_service.listener;

import com.example.catalog_service.dto.ProductActionDTO;
import com.example.catalog_service.events.ProductEvent;
import com.example.catalog_service.mapper.Mapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class ProductEventListenerImpl implements ProductEventListener {
    private final Sinks.Many<ProductEvent> sink = Sinks.many().unicast().onBackpressureBuffer();

    @Override
    public Flux<ProductEvent> productEvents(){
        return sink.asFlux();
    }

    @Override
    public void handleDeletedProduct(ProductActionDTO dto) {
        var deletedEvent = Mapper.toDeletedProductEvent().apply(dto);
        sink.tryEmitNext(deletedEvent);
    }

    @Override
    public void handleUpdatedProduct(ProductActionDTO dto) {
        var updatedEvent = Mapper.toUpdatedProductEvent().apply(dto);
        sink.tryEmitNext(updatedEvent);
    }

    @Override
    public void handleCreatedProduct(ProductActionDTO dto) {
        var createdEvent = Mapper.toCreatedProductEvent().apply(dto);
        sink.tryEmitNext(createdEvent);
    }

    @Override
    public void handleViewedProduct(ProductActionDTO dto) {
        var viewedEvent = Mapper.toViewedProductEvent().apply(dto);
        sink.tryEmitNext(viewedEvent);
    }
}
