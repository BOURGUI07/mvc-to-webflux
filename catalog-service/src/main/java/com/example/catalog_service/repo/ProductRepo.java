package com.example.catalog_service.repo;

import com.example.catalog_service.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductRepo extends ReactiveCrudRepository<Product, Long> {
    Flux<Product> findBy(Pageable pageable);
}