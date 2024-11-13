package com.example.order_service.advice;

import com.example.order_service.dto.OrderServiceProperties;
import com.example.order_service.exceptions.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.time.Instant;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final OrderServiceProperties properties;

    private ProblemDetail handleException(
            Exception ex, HttpStatus status, String title, String errorCategory, URI type, ServerWebExchange exchange) {
        var problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle(title);
        String path = exchange.getRequest().getPath().value();
        var method = exchange.getRequest().getMethod().name();
        problemDetail.setInstance(URI.create(path));
        problemDetail.setProperty("timestamp", Instant.now());
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

    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail handleProductNotFoundException(OrderNotFoundException ex, ServerWebExchange exchange) {
        return handleException(
                ex,
                HttpStatus.NOT_FOUND,
                "Order Not Found",
                "RESOURCE_NOT_FOUND",
                properties.exceptionNotFound(),
                exchange);
    }
}
