package com.example.catalog_service.service;

import com.example.catalog_service.dto.ProductCreationBulkResponse;
import com.example.catalog_service.dto.ProductCreationRequest;
import com.example.catalog_service.dto.ProductResponse;
import com.example.catalog_service.exceptions.InvalidProductRequestException;
import com.example.catalog_service.exceptions.ProductAlreadyExistsException;
import com.example.catalog_service.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class ProductCreationBulkService {
    private final ProductService service;

    private final ProductCreationBulkResponse response = ProductCreationBulkResponse.builder()
            .failure(0)
            .success(0)
            .processed(0)
            .createdProducts(new ArrayList<>())
            .errorDetails(new ArrayList<>())
            .build();

    private final AtomicInteger rowNumber = new AtomicInteger(0);


    public Mono<ProductCreationBulkResponse> createProducts(String filePath) {
        return processFile(filePath)
                .then(Mono.fromSupplier(() ->response))
                .doOnNext(dto ->log.info("The Create Products Operation Resulted in: {}", Util.write(dto)));
    }

    private Flux<ProductResponse> processFile(String filePath) {
        log.info("Processing file: {}", filePath);
        return Flux.<String>create(sink -> {
            try(var reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.incrementProcessed();
                    sink.next(line);
                }
            sink.complete();
            }catch (Exception e) {
                log.error("Error While Processing File : {}", e.getMessage());
            }
        })
                .flatMap(line -> {
                    log.info("Received Line: {}",line);
                    var rowValue = incrementAndGet();
                    return service.createProduct(Mono.fromSupplier(() -> createRequest(line)))
                            .doOnSuccess(productResponse -> {
                                handleSuccess(productResponse);
                                log.info("Added Product Response: {}", productResponse);
                            })
                            .onErrorResume(ex -> {
                                handleFailure(ex, rowValue);
                                log.info("Adding Error Detail Because of: {}", ex.getMessage());
                                return Mono.empty();
                            });
                });
    }

    private Integer incrementAndGet(){
        return rowNumber.incrementAndGet();
    }


    private ProductCreationRequest createRequest(String line){
        var array = line.split(",");
        return ProductCreationRequest.builder()
                .code(array[0])
                .name(array[1])
                .description(array[2])
                .price(new BigDecimal(array[3]))
                .quantity(Integer.parseInt(array[4]))
                .imageUrl(array[5])
                .build();
    }

    private void handleFailure(Throwable ex, int rowNumber){
        response.incrementFailure();
        response.addErrorDetail(rowNumber, ex.getMessage());
    }

    private void handleSuccess(ProductResponse productResponse){
        response.incrementSuccess();
        response.addCreatedProduct(productResponse);
    }


}
