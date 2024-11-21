package com.example.customer_service.service;

import com.example.customer_service.domain.Customer;
import com.example.customer_service.domain.CustomerPayment;
import com.example.customer_service.domain.PaymentStatus;
import com.example.customer_service.dto.PaymentDTO;
import com.example.customer_service.exceptions.ApplicationExceptions;
import com.example.customer_service.mapper.PaymentMapper;
import com.example.customer_service.repo.CustomerRepo;
import com.example.customer_service.repo.PaymentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final CustomerRepo repo;
    private final PaymentRepo paymentRepo;

    public Function<PaymentDTO.Request,Mono<PaymentDTO>> processPayment() {
       return request -> {
           var orderId = request.orderId();
           return paymentRepo.existsByOrderId(orderId)
                   .filter(Predicate.not(b->b))
                   .switchIfEmpty(ApplicationExceptions.duplicateEvent(orderId))
                   .then(repo.findById(request.customerId()))
                   .switchIfEmpty(ApplicationExceptions.customerNotFound(request.customerId()))
                   .filter(customer -> customer.getBalance().compareTo(request.amount())>=0)
                   .switchIfEmpty(ApplicationExceptions.notEnoughBalance(request.customerId()))
                   .zipWhen(customer -> Mono.fromSupplier(() -> PaymentMapper.toEntity().apply(request)), deduct())
                   .flatMap(Function.identity());
       };
    }

    private BiFunction<Customer, CustomerPayment,Mono<PaymentDTO>> deduct() {
        return (customer,payment) -> repo.save(customer.setBalance(customer.getBalance().subtract(payment.getAmount())))
                .then(paymentRepo.save(payment.setStatus(PaymentStatus.DEDUCTED)))
                .map(PaymentMapper.toDTO());
    }


    public Function<UUID,Mono<PaymentDTO>> refund(){
        return orderId -> paymentRepo.findByOrderIdAndStatus(orderId,PaymentStatus.DEDUCTED)
                .zipWhen(payment -> repo.findById(payment.getCustomerId()), executeRefund())
                .flatMap(Function.identity());

    }

    private BiFunction<CustomerPayment,Customer,Mono<PaymentDTO>> executeRefund() {
        return (payment, customer) -> repo.save(customer)
                .then(paymentRepo.save(payment))
                .map(PaymentMapper.toDTO())
                .doFirst(() -> {
                   customer.setBalance(customer.getBalance().add(payment.getAmount()));
                   payment.setStatus(PaymentStatus.REFUNDED);
                });
    }
}
