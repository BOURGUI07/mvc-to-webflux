package com.example.order_service.service;

import com.example.order_service.entity.Product;
import com.example.order_service.events.ProductEvent;
import com.example.order_service.repo.ProductRepo;
import com.example.order_service.util.Util;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepo repo;

    public Mono<Void> createProduct(ProductEvent.Created event) {
        return repo.existsByCodeIgnoreCase(event.code())
                .filter(Predicate.not(b -> b))
                .flatMap(__ -> repo.save(toEntity().apply(event)))
                .doOnNext(p -> log.info("Saved New Product Into The Database: {}", Util.write(p)))
                .then()
                .doFirst(() -> log.info("The Product Service Received Product Created Event: {}", event));
    }

    private Function<ProductEvent.Created, Product> toEntity() {
        return event -> Product.builder()
                .code(event.code())
                .productId(event.productId())
                .price(event.price())
                .build();
    }

    public Mono<Void> updateProduct(ProductEvent.Updated event) {
        return repo.findByCodeIgnoreCase(event.code())
                .flatMap(p -> repo.save(p.setPrice(event.price())))
                .doOnNext(p -> log.info("Updated New Product: {}", Util.write(p)))
                .then()
                .doFirst(() -> log.info("The Product Service Received Product Updated Event: {}", event));
    }

    public Mono<Void> deleteProduct(ProductEvent.Deleted event) {
        return repo.findByCodeIgnoreCase(event.code())
                .flatMap(repo::delete)
                .doFirst(() -> log.info("The Product Service Received Product Deleted Event: {}", event));
    }
}
