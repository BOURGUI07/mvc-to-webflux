package com.example.notification_service.service;

import com.example.notification_service.dto.NotificationServiceProperties;
import com.example.notification_service.entity.Order;
import com.example.notification_service.entity.OrderStatus;
import com.example.notification_service.events.OrderEvent;
import com.example.notification_service.exception.ApplicationExceptions;
import com.example.notification_service.exception.DuplicateEventException;
import com.example.notification_service.exception.EmailFailureException;
import com.example.notification_service.repo.CustomerRepo;
import com.example.notification_service.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender emailSender;
    private final NotificationServiceProperties properties;
    private final OrderRepo repo;
    private final CustomerRepo customerRepo;

    private static final String CREATED_SUBJECT= "Order Created Notification";
    private static final String CANCELLED_SUBJECT= "Order Cancelled Notification";
    private static final String COMPLETED_SUBJECT= "Order Completed Notification";

    private static final String GENERIC_MESSAGE = """
            ==============================
            %s
            
            ==============================
            Dear %s,
            
            Your Order With Id: %s Has Been %s!.
            
            Thanks,
            BookStore Team
            
            ===============================
            """;


    private Mono<Boolean> sendEmail(String recipient, String subject, String content) {
        return Mono.fromCallable( () ->
                {
                    try{
                        var mimeMessage = emailSender.createMimeMessage();
                        var helper = new MimeMessageHelper(mimeMessage, "utf-8");

                        helper.setFrom(properties.supportEmail());
                        helper.setTo(recipient);
                        helper.setSubject(subject);
                        helper.setText(content);
                        emailSender.send(mimeMessage);

                        log.info("Successfully sent email to {} with content {}", recipient, content);
                        return true;
                    }catch (Exception e){
                        log.error("Failed to send email", e);
                        return false;
                    }
                }
        );
    }


    private Function<OrderEvent.Created,Mono<OrderEvent.Created>> validateCreatedOrderEvent(){
        return event -> repo.existsByOrderId(event.orderId())
                .filter(Predicate.not(b->b))
                .flatMap(x -> repo.save(Order.builder().orderId(event.orderId()).build().setStatus(OrderStatus.CREATED)))
                .switchIfEmpty(ApplicationExceptions.duplicateEvent())
                .thenReturn(event);
    }

    private Function<OrderEvent.Cancelled,Mono<OrderEvent.Cancelled>> validateCancelledOrderEvent(){
        return event -> repo.findByOrderIdAndStatus(event.orderId(),OrderStatus.CREATED)
                .flatMap(x -> repo.save(x.setStatus(OrderStatus.CANCELED)))
                .map( o ->OrderEvent.Cancelled.builder()
                        .orderId(o.getOrderId())
                        .customerId(event.customerId())
                        .build());



    }

    private Function<OrderEvent.Completed,Mono<OrderEvent.Completed>> validateCompletedOrderEvent(){
        return event -> repo.findByOrderIdAndStatus(event.orderId(),OrderStatus.CREATED)
                .flatMap(x -> repo.save(x.setStatus(OrderStatus.COMPLETED)))
                .map(x -> OrderEvent.Completed.builder()
                        .orderId(x.getOrderId())
                        .customerId(event.customerId())
                        .build());


    }



    @Transactional
    public Mono<Void> sendEmailCreated(OrderEvent.Created event) {
        return validateCreatedOrderEvent().apply(event)
                .flatMap(e -> customerRepo.findByCustomerId(e.customerId())
                        .flatMap(c -> sendEmail(c.getEmail(),CREATED_SUBJECT,GENERIC_MESSAGE.formatted(
                                CREATED_SUBJECT,c.getUsername(),event.orderId().toString(),"Created")))
                        .filter(b->b)
                        .doOnNext(x -> log.info("MESSAGE SUCCESSFULLY SENT"))
                        .switchIfEmpty(ApplicationExceptions.emailFailure())
                        .then()
                )
                .doOnError(EmailFailureException.class, ex -> log.warn("Failed to send email to customer with id: {}", event.customerId()))
                .doOnError(DuplicateEventException.class, ex -> log.info("DUPLICATE EVENT"))
                .onErrorResume(EmailFailureException.class, ex -> Mono.empty())
                .onErrorResume(DuplicateEventException.class, ex -> Mono.empty());
    }


    @Transactional
    public Mono<Void> sendEmailCancelled(OrderEvent.Cancelled event) {
        return validateCancelledOrderEvent().apply(event)
                .flatMap(e -> customerRepo.findByCustomerId(e.customerId())
                        .flatMap(c -> sendEmail(c.getEmail(),CANCELLED_SUBJECT,GENERIC_MESSAGE.formatted(
                                CANCELLED_SUBJECT,c.getUsername(),event.orderId().toString(),"Cancelled")))
                        .filter(b->b)
                        .doOnNext(x -> log.info("MESSAGE SUCCESSFULLY SENT"))
                        .switchIfEmpty(ApplicationExceptions.emailFailure())
                        .then()
                )
                .doOnError(EmailFailureException.class, ex -> log.warn("Failed to send email to customer with id: {}", event.customerId()))
                .onErrorResume(EmailFailureException.class, ex -> Mono.empty());
    }

    @Transactional
    public Mono<Void> sendEmailCompleted(OrderEvent.Completed event) {
        return validateCompletedOrderEvent().apply(event)
                .flatMap(e -> customerRepo.findByCustomerId(e.customerId())
                        .flatMap(c -> sendEmail(c.getEmail(),COMPLETED_SUBJECT,GENERIC_MESSAGE.formatted(
                                COMPLETED_SUBJECT,c.getUsername(),event.orderId().toString(),"Completed")))
                        .filter(b->b)
                        .doOnNext(x -> log.info("MESSAGE SUCCESSFULLY SENT"))
                        .switchIfEmpty(ApplicationExceptions.emailFailure())
                        .then()
                )
                .doOnError(EmailFailureException.class, ex -> log.warn("Failed to send email to customer with id: {}", event.customerId()))
                .onErrorResume(EmailFailureException.class, ex -> Mono.empty());
    }
}
