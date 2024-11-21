package com.example.customer_service.service;

import com.example.customer_service.dto.CustomerDTO;
import com.example.customer_service.dto.CustomerServiceProperties;
import com.example.customer_service.dto.PageResult;
import com.example.customer_service.exceptions.ApplicationExceptions;
import com.example.customer_service.mapper.CustomerMapper;
import com.example.customer_service.repo.CustomerRepo;
import com.example.customer_service.validator.CustomerRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepo repo;
    private final CustomerServiceProperties properties;


    @Transactional
    public Mono<CustomerDTO> create(Mono<CustomerDTO.Request> dto) {
        return dto.transform(CustomerRequestValidator.validate())
                .flatMap(req ->
                        repo.existsByEmailOrUsername(req.email(),req.username())
                                .filter(Predicate.not(b->b))
                                .switchIfEmpty(ApplicationExceptions.alreadyExists(req.email(),req.username()))
                                .thenReturn(req)
                        )
                .map(CustomerMapper.toEntity())
                .flatMap(repo::save)
                .map(CustomerMapper.toDTO());

    }

    public Mono<PageResult<CustomerDTO>> findAll(int page){
        var pageNumber = page >=1 ? page - 1: 0;
        var sort = Sort.by("balance").descending();
        var pageable = PageRequest.of(pageNumber, properties.defaultPageSize(), sort);
        return Mono.zip(repo.findBy(pageable).map(CustomerMapper.toDTO()).collectList(), repo.count())
                .map(x -> toPagedResult(x.getT1(),x.getT2(),pageNumber, properties.defaultPageSize()));
    }

    public PageResult<CustomerDTO> toPagedResult(List<CustomerDTO> list, Long count, int page, int size) {
        var totalPages = (int) Math.ceil((double) count / size);
        var isFirst = page==0;
        var isLast = page==totalPages-1;
        return PageResult.<CustomerDTO>builder()
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
