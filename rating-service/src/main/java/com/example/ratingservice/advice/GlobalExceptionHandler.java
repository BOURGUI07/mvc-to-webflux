package com.example.ratingservice.advice;

import com.example.ratingservice.dto.RatingServiceProperties;
import com.example.ratingservice.exceptions.InvalidRatingRequestException;
import com.example.ratingservice.exceptions.RatingNotFoundException;
import com.example.ratingservice.util.Util;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final RatingServiceProperties properties;

    private ResponseEntity<ProblemDetail> handleException(
            Exception ex, HttpStatus status, String title, String errorCategory, URI type, ServerWebExchange exchange) {
        var problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle(title);

        String path = exchange.getRequest().getPath().value();
        var method = exchange.getRequest().getMethod().name();

        var date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        problemDetail.setInstance(URI.create(path));
        problemDetail.setProperty("date", date);
        problemDetail.setProperty("errorCategory", errorCategory);
        problemDetail.setProperty("service", properties.name());
        problemDetail.setType(type);
        problemDetail.setProperty("httpMethod", method);

        log.error("Problem Detail Response: {}", Util.write(problemDetail));

        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneralException(Exception ex, ServerWebExchange exchange) {
        return handleException(
                ex,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "General Error",
                "GENERAL_ERROR",
                properties.exceptionGeneral(),
                exchange);
    }

    @ExceptionHandler(RatingNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleRatingNotFoundException(
            RatingNotFoundException ex, ServerWebExchange exchange) {
        return handleException(
                ex,
                HttpStatus.NOT_FOUND,
                "Rating Not Found",
                "RESOURCE_NOT_FOUND",
                properties.exceptionNotFound(),
                exchange);
    }

    @ExceptionHandler(InvalidRatingRequestException.class)
    public ResponseEntity<ProblemDetail> handleInvalidRequest(
            InvalidRatingRequestException ex, ServerWebExchange exchange) {
        return handleException(
                ex,
                HttpStatus.BAD_REQUEST,
                "Invalid Rating Request",
                "INVALID_REQUEST",
                properties.exceptionBadRequest(),
                exchange);
    }
}
