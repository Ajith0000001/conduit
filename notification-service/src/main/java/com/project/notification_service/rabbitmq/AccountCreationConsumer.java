package com.project.notification_service.rabbitmq;

import java.io.IOException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.project.dtos.AccountCreationEvent;
import com.project.notification_service.service.EmailService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AccountCreationConsumer {


    final private EmailService emailService;


    @RabbitListener(queues = RabbitmqConfig.notificationQueue)
    public void receiveMessage(AccountCreationEvent event) throws IOException {

            log.info("Account Number {}",event.getAccountNumber());

        try {
             emailService.sendAccountCreatedMail(
        "ajithdhas003@gmail.com",
        "Account Number: " + event.getAccountNumber());


        log.info("Payload: {}",event.getAccountNumber());
            
        } catch (Exception e) {
            log.info("Error [{}]", e.getMessage());
        }

        
    }
    
}