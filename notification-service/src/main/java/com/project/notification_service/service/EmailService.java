package com.project.notification_service.service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendAccountCreatedMail(
            String to,
            String accountNumber) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Account Created");
        message.setText(
                "Your account was created successfully.\n"
                + "Account Number: " + accountNumber
        );

        mailSender.send(message);
    }
}