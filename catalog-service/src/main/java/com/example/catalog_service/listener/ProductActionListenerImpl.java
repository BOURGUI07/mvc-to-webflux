package com.example.catalog_service.listener;

import com.example.catalog_service.dto.ProductActionDTO;
import com.example.catalog_service.events.ProductEvent;
import com.example.catalog_service.mapper.Mapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;

/**
 * Receive the dto, convert it into a product-event
 * then emit it via a sink. so that the producer() will consume it
 */
@Service
public class ProductActionListenerImpl implements ProductActionListener {
    private final Sinks.Many<ProductEvent> sink = Sinks.many().unicast().onBackpressureBuffer();

    @Override
    public Flux<ProductEvent> productEvents(){
        return sink.asFlux();
    }

    @Override
    public void handleDeletedProduct(ProductActionDTO dto) {
         handleEvent().accept(Mapper.toDeletedProductEvent().apply(dto));
    }

    @Override
    public void handleUpdatedProduct(ProductActionDTO dto) {
        handleEvent().accept(Mapper.toUpdatedProductEvent().apply(dto));
    }

    @Override
    public void handleCreatedProduct(ProductActionDTO dto) {
        handleEvent().accept(Mapper.toCreatedProductEvent().apply(dto));
    }

    @Override
    public void handleViewedProduct(ProductActionDTO dto) {
        handleEvent().accept(Mapper.toViewedProductEvent().apply(dto));
    }

    private Consumer<ProductEvent> handleEvent(){
        return sink::tryEmitNext;
    }
}
