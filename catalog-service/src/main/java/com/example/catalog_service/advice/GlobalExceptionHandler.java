package com.example.catalog_service.advice;

import com.example.catalog_service.dto.CatalogServiceProperties;
import com.example.catalog_service.exceptions.InvalidProductRequestException;
import com.example.catalog_service.exceptions.ProductAlreadyExistsException;
import com.example.catalog_service.exceptions.ProductNotFoundException;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final CatalogServiceProperties properties;

    private ProblemDetail handleException(
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
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex, ServerWebExchange exchange) {
        return handleException(
                ex,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "General Error",
                "GENERAL_ERROR",
                properties.exceptionGeneral(),
                exchange);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ProblemDetail handleProductNotFoundException(ProductNotFoundException ex, ServerWebExchange exchange) {
        return handleException(
                ex,
                HttpStatus.NOT_FOUND,
                "Product Not Found",
                "RESOURCE_NOT_FOUND",
                properties.exceptionNotFound(),
                exchange);
    }

    @ExceptionHandler(InvalidProductRequestException.class)
    public ProblemDetail handleInvalidRequest(InvalidProductRequestException ex, ServerWebExchange exchange) {
        return handleException(
                ex,
                HttpStatus.BAD_REQUEST,
                "Invalid Product Request",
                "INVALID_REQUEST",
                properties.exceptionBadRequest(),
                exchange);
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ProblemDetail handleAlreadyExistingProduct(ProductAlreadyExistsException ex, ServerWebExchange exchange) {
        return handleException(
                ex,
                HttpStatus.BAD_REQUEST,
                "Product Already Exists",
                "INVALID_REQUEST",
                properties.exceptionBadRequest(),
                exchange);
    }
}
