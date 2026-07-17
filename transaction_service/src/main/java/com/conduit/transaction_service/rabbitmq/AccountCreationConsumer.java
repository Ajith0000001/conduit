package com.conduit.transaction_service.rabbitmq;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.conduit.transaction_service.model.AuditLog;
import com.conduit.transaction_service.repo.AuditLogRepo;
import com.rabbitmq.client.Channel;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class AccountCreationConsumer {

    final private RabbitTemplate rabbitTemplate;
    final private AuditLogRepo auditLogRepo;


    @RabbitListener(queues = RabbitmqConfig.transactionQueue)
    public void receiveMessage(String payload, Channel channel, Message message) throws IOException {

        long dId = message.getMessageProperties().getDeliveryTag();

        try {
            String eventId = payload.split(",")[1];
         log.info("Received deliveryTag={}",
            message.getMessageProperties().getDeliveryTag());

            AuditLog log = new AuditLog();
            String event = message.getMessageProperties().getReceivedRoutingKey();
            log.setEvent(event);
            log.setEventId(eventId);

        auditLogRepo.save(log);

    // log.info("Headers={}",
    //         message.getMessageProperties().getHeaders());
        throw new RuntimeException("Db is down");
        // channel.basicAck(dId, false);
            
        } catch(DataIntegrityViolationException e) {
            log.info("Databse duplication: {}",e.getMessage());
            channel.basicAck(dId, false);
            return;
        }
         catch (Exception e) {

            long count = getRetryCount(message);
            // log.info("retry count {}", count);

            if(count >= 5) {
                log.info("death queue");
                rabbitTemplate.convertAndSend("final-dlx","failed",payload);
                channel.basicAck(dId, false);
                return;
            }

            channel.basicNack(dId, false, false);
        }

        
    }

    @SuppressWarnings("unchecked")
    public long getRetryCount(Message message) {

         List<Map<String, ?>> deaths =
            (List<Map<String, ?>>) message.getMessageProperties().getHeaders().get("x-death");

            // log.info("retry count {}",deaths);

            if(deaths == null || deaths.isEmpty()) return 0;

            long count = (long) deaths.get(0).get("count");

            return count;


    }
    
}
