package com.project.notification_service.rabbitmq;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.project.notification_service.events.AccountCreatedEvent;
import com.rabbitmq.client.Channel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AccountCreationConsumer {


    @RabbitListener(queues = RabbitmqConfig.notificationQueue)
    public void receiveMessage(String payload) throws IOException {


        try {
        log.info("Payload: {}",payload);
        // channel.basicAck(dId, false);
            
        } catch (Exception e) {
            // channel.basicNack(dId, false, false);
        }

        
    }
    
}