package com.example.ratingservice.controller;

import static com.example.ratingservice.util.Constants.ApiBaseUrl.RATING_BASE_URL;

import com.example.ratingservice.dto.request.RatingCreationRequest;
import com.example.ratingservice.dto.request.RatingUpdateRequest;
import com.example.ratingservice.dto.response.PaginatedRatingResponse;
import com.example.ratingservice.dto.response.RatingResponse;
import com.example.ratingservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(RATING_BASE_URL)
public class RatingController {

    private final RatingService service;

    @GetMapping("/customers/{customerId}")
    public Mono<ResponseEntity<PaginatedRatingResponse>> getRatingsByCustomerId(
            @PathVariable Long customerId, @RequestParam(defaultValue = "1", required = false) int page) {
        return service.findByCustomerId(customerId, page).map(ResponseEntity::ok);
    }

    @GetMapping("/products/{productId}")
    public Mono<ResponseEntity<PaginatedRatingResponse>> getRatingsByProductId(
            @PathVariable Long productId, @RequestParam(defaultValue = "1", required = false) int page) {
        return service.findByProductId(productId, page).map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<RatingResponse>> createRating(@RequestBody Mono<RatingCreationRequest> request) {
        return service.createRating(request)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }

    @PutMapping("/{ratingId}")
    public Mono<ResponseEntity<RatingResponse>> updateRating(
            @PathVariable Long ratingId, @RequestBody Mono<RatingUpdateRequest> request) {
        return service.updateRating(ratingId, request).map(ResponseEntity::ok);
    }

    @DeleteMapping("/{ratingId}")
    public Mono<ResponseEntity<Void>> deleteRating(@PathVariable Long ratingId) {
        return service.deleteRating(ratingId)
                .then(Mono.fromSupplier(() -> ResponseEntity.noContent().build()));
    }
}
