package com.example.order_service.repo;

import com.example.order_service.entity.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepo extends ReactiveCrudRepository<Product, Long> {

    Mono<Product> findByCodeIgnoreCase(String code);

    Mono<Product> findByProductId(Long productId);

    Mono<Boolean> existsByCodeIgnoreCase(String code);
}
