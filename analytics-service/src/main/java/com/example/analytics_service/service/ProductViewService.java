package com.example.analytics_service.service;

import com.example.analytics_service.dto.ProductViewDTO;
import com.example.analytics_service.entity.ProductView;
import com.example.analytics_service.events.ProductEvent;
import com.example.analytics_service.mapper.Mapper;
import com.example.analytics_service.repo.ProductViewRepo;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductViewService {

    private final ProductViewRepo repo;

    /**
     * Return the top 5 most viewed products in descending order
     */
    public Flux<ProductViewDTO> products() {
       return  repo.findTop5ByOrderByViewCountDesc()
                .map(Mapper.toDTO());
    }

    /**
     * Find the product by code
     * if it does exist, increase its viewing count by one
     * if it doesn't, create a new one with 1 as view count
     * then save into the DB
     */
    public Function<ProductEvent.View,Mono<Void>> consume(){
        return event -> repo.findByProductCode(event.code())
                .doOnNext(p -> p.setViewCount(p.getViewCount()+1))
                .defaultIfEmpty(ProductView.builder().viewCount(1L).productCode(event.code()).build())
                .flatMap(repo::save)
                .then();
    }
}
