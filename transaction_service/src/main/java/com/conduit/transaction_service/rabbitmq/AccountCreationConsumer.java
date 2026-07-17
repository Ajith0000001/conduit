package com.conduit.transaction_service.rabbitmq;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.conduit.transaction_service.model.AuditLog;
import com.conduit.transaction_service.model.TransactionHistory;
import com.conduit.transaction_service.repo.AuditLogRepo;
import com.conduit.transaction_service.repo.TransactionHistoryRepo;
import com.project.dtos.AccountCreationEvent;
import com.rabbitmq.client.Channel;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class AccountCreationConsumer {

    final private RabbitTemplate rabbitTemplate;
    final private AuditLogRepo auditLogRepo;
    final private TransactionHistoryRepo transactionHistoryRepo;


    @RabbitListener(queues = RabbitmqConfig.transactionQueue)
    public void receiveMessage(AccountCreationEvent payload, Channel channel, Message message) throws IOException {

        if(payload == null || payload.getEventId() == null) throw new RuntimeException("Payload missing");

        long dId = message.getMessageProperties().getDeliveryTag();

        try {
            String eventId = payload.getEventId();
         log.info("Received deliveryTag={}",
            message.getMessageProperties().getDeliveryTag());

        //     AuditLog log = new AuditLog();
        // log.setEvent(event);
        //     log.setEventId(eventId);
        // auditLogRepo.save(log);
        // throw new RuntimeException("Db is down");

        TransactionHistory history = new TransactionHistory();
        history.setSenderAccount(payload.getAccountNumber());
        history.setReceiverAccount(payload.getAccountNumber());
        history.setAmount(payload.getBalance());
        history.setTransactionType("DEPOSIT");
        history.setDescription("Account creation initial deposit");
        history.setCreatedAt(LocalDateTime.now());
        history.setUpdatedAt(LocalDateTime.now());

        transactionHistoryRepo.save(history);

        channel.basicAck(dId, false);
            
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
