package com.example.customer_service.service;

import com.example.customer_service.dto.CustomerDTO;
import com.example.customer_service.dto.CustomerServiceProperties;
import com.example.customer_service.dto.PageResult;
import com.example.customer_service.events.CustomerEvent;
import com.example.customer_service.exceptions.ApplicationExceptions;
import com.example.customer_service.mapper.CustomerMapper;
import com.example.customer_service.mapper.EventMapper;
import com.example.customer_service.repo.CustomerRepo;
import com.example.customer_service.util.Util;
import com.example.customer_service.validator.CustomerRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.function.Predicate;

/***
 * the customer-service is responsible for standard CRUD operations
 * such as Post and Get Requests
 * It's responsible for sending the CreatedCustomerEvents as well
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomerService {
    private final CustomerRepo repo;
    private final CustomerServiceProperties properties;
    private final Sinks.Many<CustomerEvent> sink = Sinks.many().unicast().onBackpressureBuffer();

    public Flux<CustomerEvent> events(){
        return sink.asFlux();
    }

    /**
     * Received a mono of CustomerRequestDTO
     * validate the request field nullability
     * then make sure the username and email don't  already exists in the DB
     * convert the dto into an entity
     * save the entity
     * convert the entity into ResponseDTO
     * Asynchronously convert the dto into CreatedCustomerEvent and put it into a sink
     */

    @Transactional
    public Mono<CustomerDTO> create(Mono<CustomerDTO.Request> dto) {
        return dto.transform(CustomerRequestValidator.validate())
                .flatMap(req ->
                        repo.existsByEmail(req.email())
                                .filter(Predicate.not(b->b))
                                .switchIfEmpty(ApplicationExceptions.alreadyExists(String.format("Customer with email %s already exists", req.email())))
                                .then(repo.existsByUsername(req.username()))
                                .filter(Predicate.not(b->b))
                                .switchIfEmpty(ApplicationExceptions.alreadyExists(String.format("Customer with username %s already exists", req.username())))
                                .thenReturn(req)
                        )
                .map(CustomerMapper.toEntity())
                .flatMap(repo::save)
                .map(CustomerMapper.toDTO())
                .doOnNext(x -> sink.tryEmitNext(EventMapper.toCustomerEvent().apply(((CustomerDTO.Response) x))))
                .doOnNext(res -> log.info("Created New Customer: {}", Util.write(res)));

    }



    public Mono<PageResult<CustomerDTO.Response>> findAll(int page){
        var pageNumber = page >=1 ? page - 1: 0;
        var sort = Sort.by("balance").descending();
        var pageable = PageRequest.of(pageNumber, properties.defaultPageSize(), sort);
        return Mono.zip(repo.findBy(pageable).map(CustomerMapper.toDTO()).cast(CustomerDTO.Response.class).collectList(), repo.count())
                .map(x -> toPagedResult(x.getT1(),x.getT2(),pageNumber, properties.defaultPageSize()))
                .doOnNext(res -> log.info("Find All Customers by Page {} Resulted In: {}",page,Util.write(res)));
    }

    public PageResult<CustomerDTO.Response> toPagedResult(List<CustomerDTO.Response> list, Long count, int page, int size) {
        var totalPages = (int) Math.ceil((double) count / size);
        var isFirst = page==0;
        var isLast = page==totalPages-1;
        return PageResult.<CustomerDTO.Response>builder()
                .totalElements(count)
                .pageNumber(page)
                .data(list)
                .totalPages(totalPages)
                .isFirst(isFirst)
                .isLast(isLast)
                .hasNext(!isLast)
                .hasPrevious(!isFirst)
                .pageNumber(page + 1)
                .build();
    }
}
