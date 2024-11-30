package com.example.customer_service.advice;

import com.example.customer_service.dto.CustomerServiceProperties;
import com.example.customer_service.exceptions.CustomerAlreadyExistsException;
import com.example.customer_service.exceptions.CustomerNotFoundException;
import com.example.customer_service.exceptions.InvalidRequestException;
import com.example.customer_service.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
    private final CustomerServiceProperties properties;

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

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleProductNotFoundException(CustomerNotFoundException ex, ServerWebExchange exchange) {
        return handleException(
                ex,
                HttpStatus.NOT_FOUND,
                "Customer Not Found",
                "RESOURCE_NOT_FOUND",
                properties.exceptionNotFound(),
                exchange);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ProblemDetail> handleInvalidRequest(InvalidRequestException ex, ServerWebExchange exchange) {
        return handleException(
                ex,
                HttpStatus.BAD_REQUEST,
                "Invalid Customer Request",
                "INVALID_REQUEST",
                properties.exceptionBadRequest(),
                exchange);
    }

    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleAlreadyExistingProduct(CustomerAlreadyExistsException ex, ServerWebExchange exchange) {
        return handleException(
                ex,
                HttpStatus.BAD_REQUEST,
                "Customer Already Exists",
                "INVALID_REQUEST",
                properties.exceptionBadRequest(),
                exchange);
    }
}
