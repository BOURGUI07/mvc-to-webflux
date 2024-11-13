package com.example.catalog_service.web;

import com.example.catalog_service.domain.Product;
import com.example.catalog_service.dto.PagedResult;
import com.example.catalog_service.dto.ProductCreationRequest;
import com.example.catalog_service.dto.ProductResponse;
import com.example.catalog_service.dto.ProductUpdateRequest;
import com.example.catalog_service.service.ProductService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService service;

    @GetMapping
    public Mono<PagedResult<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "1", required = false) int page) {
        return service.getProducts(page)
                .doFirst(()->log.info("........Web Layer::Get Products Of Page: {}..........", page));
    }

    @GetMapping(value = "/stream/{maxPrice}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductResponse> streamProducts(@PathVariable BigDecimal maxPrice) {
        return service.getProductStream(maxPrice)
                .doFirst(()-> log.info("..........Web Layer::Get Products Stream of Max Price: {}............", maxPrice));
    }

    @GetMapping("/{code}")
    public Mono<ProductResponse> getProduct(@PathVariable String code) {
        return service.findByCode(code)
                .doFirst(()->log.info("..........Web Layer::Get Product by Code: {}.............", code));
    }


    @PostMapping
    public Mono<ProductResponse> create(@RequestBody Mono<ProductCreationRequest> request) {
        return service.createProduct(request)
                .doFirst(()->log.info("..........Web Layer::Create Product : {}", request));
    }

    @PutMapping("/{code}")
    public Mono<ProductResponse> update(@PathVariable String code,@RequestBody Mono<ProductUpdateRequest> request) {
        return service.update(code, request)
                .doFirst(()->log.info("..........Web Layer::Update Product With Code: {}", code));
    }

    @DeleteMapping("/{code}")
    public Mono<Void> delete(@PathVariable String code) {
        return service.deleteByCode(code)
                .doFirst(()->log.info("..........Web Layer::Delete Product With Code: {}", code));
    }
}
