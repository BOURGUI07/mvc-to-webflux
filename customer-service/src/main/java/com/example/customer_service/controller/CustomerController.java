package com.example.customer_service.controller;

import com.example.customer_service.dto.CustomerDTO;
import com.example.customer_service.dto.PageResult;
import com.example.customer_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Mono<PageResult<CustomerDTO.Response>> findAll(
            @RequestParam(defaultValue = "1",required = false) int page
    ){
        return customerService.findAll(page);
    }

    @PostMapping
    public Mono<CustomerDTO> create(@RequestBody Mono<CustomerDTO.Request> request){
        return customerService.create(request);
    }
}