package com.example.customer_service.controller;

import com.example.customer_service.dto.CustomerDTO;
import com.example.customer_service.dto.PageResult;
import com.example.customer_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.example.customer_service.util.Constants.PublicApiUrls.CUSTOMER_BASE_URL;

@RequiredArgsConstructor
@RestController
@RequestMapping(CUSTOMER_BASE_URL)
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Mono<ResponseEntity<PageResult<CustomerDTO.Response>>> findAll(
            @RequestParam(defaultValue = "1",required = false) int page
    ){
        return customerService.findAll(page)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<CustomerDTO>> create(@RequestBody Mono<CustomerDTO.Request> request){
        return customerService.create(request)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }
}
