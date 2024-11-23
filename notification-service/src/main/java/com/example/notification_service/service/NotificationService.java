package com.example.notification_service.service;

import com.example.notification_service.dto.NotificationServiceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender emailSender;
    private final NotificationServiceProperties properties;

    private static final String CREATED_SUBJECT= "Order Created Notification";
    private static final String CANCELLED_SUBJECT= "Order Cancelled Notification";
    private static final String COMPLETED_SUBJECT= "Order Completed Notification";


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
                        return true;
                    }catch (Exception e){
                        log.error("Failed to send email", e);
                        return false;
                    }
                }
        );
    }




}
