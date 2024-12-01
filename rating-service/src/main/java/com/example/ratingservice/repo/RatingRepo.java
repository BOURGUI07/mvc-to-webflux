package com.example.ratingservice.repo;

import com.example.ratingservice.entity.Rating;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RatingRepo extends ReactiveCrudRepository<Rating, Long> {

    Flux<Rating> findByProductId(Long id, Pageable pageable);
    Flux<Rating> findByCustomerId(Long id, Pageable pageable);
}
