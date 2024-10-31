package com.example.catalog_service.service;

import com.example.catalog_service.dto.CatalogServiceProperties;
import com.example.catalog_service.dto.PagedResult;
import com.example.catalog_service.dto.ProductCreationResponse;
import com.example.catalog_service.exceptions.ApplicationsExceptions;
import com.example.catalog_service.mapper.Mapper;
import com.example.catalog_service.repo.ProductRepo;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepo repo;
    private final CatalogServiceProperties properties;

    public Flux<ProductCreationResponse> getProductStream(BigDecimal maxPrice) {
        log.info("......Service Layer::Get Products Stream of Max Price: {} ......", maxPrice);
        return repo.findAll().map(Mapper::toDto).filter(x -> x.price().compareTo(maxPrice) < 0);
    }

    public Mono<PagedResult<ProductCreationResponse>> getProducts(int page) {
        log.info(
                "......Service Layer::Get Products Of Page: {} And Size: {} ......",
                page,
                properties.defaultPageSize());
        var pageNumber = page >= 1 ? page - 1 : 0;
        var sort = Sort.by("name").ascending();
        var pageable = PageRequest.of(pageNumber, properties.defaultPageSize(), sort);
        return Mono.zip(repo.findBy(pageable).map(Mapper::toDto).collectList(), repo.count())
                .map(x -> Mapper.toPagedResult(x.getT1(), x.getT2(), pageNumber, properties.defaultPageSize()));
    }

    public Mono<ProductCreationResponse> findByCode(String code) {

        return repo.findByCodeIgnoreCase(code)
                .switchIfEmpty(ApplicationsExceptions.productNotFound(code))
                .map(Mapper::toDto);
    }
}
