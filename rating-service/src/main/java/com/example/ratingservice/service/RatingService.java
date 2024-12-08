package com.example.ratingservice.service;

import com.example.ratingservice.dto.RatingServiceProperties;
import com.example.ratingservice.dto.request.RatingCreationRequest;
import com.example.ratingservice.dto.request.RatingUpdateRequest;
import com.example.ratingservice.dto.response.PaginatedRatingResponse;
import com.example.ratingservice.dto.response.RatingResponse;
import com.example.ratingservice.entity.OrderHistory;
import com.example.ratingservice.entity.Rating;
import com.example.ratingservice.exceptions.ApplicationExceptions;
import com.example.ratingservice.exceptions.InvalidRatingRequestException;
import com.example.ratingservice.mapper.RatingMapper;
import com.example.ratingservice.repo.OrderHistoryRepo;
import com.example.ratingservice.repo.RatingRepo;
import com.example.ratingservice.validator.RatingRequestValidator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * The rating-service is responsible for standard CRUD endpoints.
 */


@Service
@RequiredArgsConstructor
@Slf4j
public class RatingService {
    private final OrderHistoryRepo orderRepo;
    private final RatingRepo ratingRepo;
    private final RatingServiceProperties properties;

    /**
     * Receive the request
     * validate the fields nullability
     * make sure the orderId exists
     * make sure the request productId and customerId equals to the existing OrderHistory entity
     * convert the request into entity
     * save the entity
     * convert the saved entity into response dto
     */
    @Transactional
    public Mono<RatingResponse> createRating(Mono<RatingCreationRequest> request) {
        return RatingRequestValidator.validate()
                .andThen(validateFields())
                .apply(request)
                .flatMap(req -> ratingRepo.save(RatingMapper.toEntity().apply(req)))
                .map(RatingMapper.toDto());
    }

    private UnaryOperator<Mono<RatingCreationRequest>> validateFields() {
        return mono -> mono.zipWhen(request -> orderRepo
                        .findByOrderId(request.orderId())
                        .switchIfEmpty(ApplicationExceptions.invalidRequest(
                                String.format("Order id %s not found", request.orderId()))))
                .flatMap(x -> {
                    var request = x.getT1();
                    var orderHistory = x.getT2();

                    return Mono.fromSupplier(() -> request)
                            .filter(__ -> hasValidProductAndCustomer().test(request, orderHistory))
                            .switchIfEmpty(ApplicationExceptions.invalidRequest(
                                    "Either productId or customerId doesn't match existing productId or customerId."));
                });
    }

    private BiPredicate<RatingCreationRequest, OrderHistory> hasValidProductAndCustomer() {
        return (request, orderHistory) -> Objects.equals(request.customerId(), orderHistory.getCustomerId())
                && Objects.equals(request.productId(), orderHistory.getProductId());
    }


    /**
     * make sure the ratingId exists. if not, raise notFound exception
     * update the fields. The user has to update at least one field.
     * save the rating entity
     * convert the saved entity into response dto
     */
    @Transactional
    public Mono<RatingResponse> updateRating(Long ratingId, Mono<RatingUpdateRequest> request) {
        return ratingRepo
                .findById(ratingId)
                .switchIfEmpty(ApplicationExceptions.notFound(ratingId))
                .zipWhen(rating -> request, executeUpdate())
                .flatMap(Function.identity());
    }

    private BiFunction<Rating, RatingUpdateRequest, Mono<RatingResponse>> executeUpdate() {
        return (rating, request) -> {
            Optional.ofNullable(request.content()).ifPresent(rating::setContent);
            Optional.ofNullable(request.title()).ifPresent(rating::setTitle);
            Optional.ofNullable(request.value()).ifPresent(value -> {
                if (value <= 5 && value >= 1) {
                    rating.setValue(value);
                } else {
                    throw new InvalidRatingRequestException("Rating value should be between 1 and 5");
                }
            });
            return ratingRepo.save(rating).map(RatingMapper.toDto());
        };
    }


    /**
     * find the rating entities by customerId
     * convert it into dto
     * convert the flux into list
     * convert the list into paginated response
     */
    public Mono<PaginatedRatingResponse> findByCustomerId(Long customerId, int page) {
        var pageNumber = page >= 1 ? page - 1 : 0;
        PageRequest pageable = PageRequest.of(
                pageNumber, properties.defaultPageSize(), Sort.by("ratingId").ascending());
        return ratingRepo
                .findByCustomerId(customerId, pageable)
                .map(RatingMapper.toDto())
                .collectList()
                .map(toPaginatedResponse());
    }

    public Mono<PaginatedRatingResponse> findByProductId(Long productId, int page) {
        var pageNumber = page >= 1 ? page - 1 : 0;
        PageRequest pageable = PageRequest.of(
                pageNumber, properties.defaultPageSize(), Sort.by("ratingId").ascending());
        return ratingRepo
                .findByProductId(productId, pageable)
                .map(RatingMapper.toDto())
                .collectList()
                .map(toPaginatedResponse());
    }

    private Function<List<RatingResponse>, PaginatedRatingResponse> toPaginatedResponse() {
        return list -> PaginatedRatingResponse.builder()
                .ratings(list)
                .count((long) list.size())
                .averageRating(list.stream()
                        .mapToDouble(RatingResponse::value)
                        .average()
                        .orElse(-1))
                .minRating(
                        list.stream().mapToDouble(RatingResponse::value).min().orElse(-1))
                .maxRating(
                        list.stream().mapToDouble(RatingResponse::value).max().orElse(-1))
                .build();
    }

    @Transactional
    public Mono<Void> deleteRating(Long ratingId) {
        return ratingRepo
                .findById(ratingId)
                .switchIfEmpty(ApplicationExceptions.notFound(ratingId))
                .flatMap(ratingRepo::delete);
    }
}
