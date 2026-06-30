package com.project.conduit.controller;

import java.io.IOException;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.conduit.rabbitmq.RabbitmqConfig;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
public class MessageController {

    final private RabbitTemplate rabbitTemplate;

    @GetMapping("/msg")
    public void sendMessage() throws IOException{

        for(int i = 0; i < 15; ++i) {
        
        rabbitTemplate.convertAndSend(RabbitmqConfig.transactionQueue,"Hello " + i);

        }
    }
    
}
