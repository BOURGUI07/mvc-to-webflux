package com.example.catalog_service.web;

import com.example.catalog_service.dto.PagedResult;
import com.example.catalog_service.dto.ProductCreationResponse;
import com.example.catalog_service.service.ProductService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Operators.as;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService service;

    @GetMapping
    public Mono<PagedResult<ProductCreationResponse>> getProducts(
            @RequestParam(defaultValue = "1", required = false) int page) {
        log.info("........Web Layer::Get Products Of Page: {}..........", page);
        return service.getProducts(page);
    }

    @GetMapping(value = "/stream/{maxPrice}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductCreationResponse> streamProducts(@PathVariable BigDecimal maxPrice) {
        log.info("..........Web Layer::Get Products Stream of Max Price: {}............", maxPrice);
        return service.getProductStream(maxPrice);
    }

    @GetMapping("/{code}")
    public Mono<ProductCreationResponse> getProduct(@PathVariable String code) {
         log.info("..........Web Layer::Get Product by Code: {}.............",code);
               return   service.findByCode(code);

    }
}
