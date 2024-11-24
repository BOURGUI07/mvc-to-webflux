package com.example.analytics_service.repo;

import com.example.analytics_service.entity.ProductView;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductViewRepo extends ReactiveCrudRepository<ProductView, Long> {

    Mono<ProductView> findByProductCode(String code);
    Mono<Boolean> existsByProductCode(String code);
    Flux<ProductView> findTop5ByOrderByViewCountDesc();
}
