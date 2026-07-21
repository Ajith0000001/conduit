package com.project.account_service.service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.account_service.model.Account;
import com.project.account_service.model.OutboxEvent;
import com.project.account_service.repo.AccountRepo;
import com.project.account_service.repo.OutboxRepo;
import com.project.account_service.socket.parser.AccountCreation;
import com.project.account_service.socket.parser.TransferMoney;
import com.project.dtos.AccountCreationEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class BankService {

    private final AccountRepo accountRepo;
    private final JdbcTemplate jdbcTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final OutboxRepo outboxRepo;
    private final ObjectMapper objectMapper;


    @Transactional
    public String accountCreation(AccountCreation accountCreation) throws Exception {

        String traceId = UUID.randomUUID().toString().substring(7);


        if(accountCreation.getInitalAmount() == 0 ) return null;

        Account account = new Account();
        account.setAccountNumber(UUID.randomUUID().toString());
        account.setBalance(BigDecimal.valueOf(accountCreation.getInitalAmount()));
        account.setBlocked(false);
         account = accountRepo.save(account);

         if(account == null) throw new RuntimeException("Account creation failed in db");

         log.info("[{}] Account created with account id {}",traceId,account.getAccountId());
         String eventId = UUID.randomUUID().toString();
         
         AccountCreationEvent event = new AccountCreationEvent();
         event.setAccountId(account.getAccountId());
         event.setAccountNumber(account.getAccountNumber());
         event.setBalance(account.getBalance());
         event.setBlocked(account.isBlocked());
         event.setEventId(eventId);

         String payload = objectMapper.writeValueAsString(event);
         CorrelationData correlationData = new CorrelationData();
         correlationData.setId(UUID.randomUUID().toString());


         OutboxEvent outboxEvent = new OutboxEvent();
         outboxEvent.setCreatedAt(LocalDateTime.now());
         outboxEvent.setEventType("ACCOUNT_CREATED");
         outboxEvent.setPayload(payload);
         outboxEvent.setStatus("PENDING");
         outboxEvent.setEvent_id(eventId);
         outboxEvent.setCorrelationId(correlationData.getId());

         outboxRepo.save(outboxEvent);
          log.info("[{}] Account outbox event created with id of {}" , traceId,eventId);


        rabbitTemplate.convertAndSend("account-events", "account.created",event,correlationData);

        log.info("Account creation Successfully",account);

        return account.getAccountNumber();


    }

    public void deleteAccount(int accountId) {
        accountRepo.deleteById(accountId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void transferMoney(TransferMoney transferMoney) {

        String receiverAccNo = transferMoney.getReceiver();
        String senderAccNo = transferMoney.getSender();
        int amount = transferMoney.getAmount();

        log.info("senderAcc {}",senderAccNo);
        log.info("receiverAcc {}",receiverAccNo);
        log.info("amount {}",amount);
        

        Account senderAccount = accountRepo.getByAccountNo(senderAccNo);
        Account receiverAccount = accountRepo.getByAccountNo(receiverAccNo);

        if(senderAccount == null || receiverAccount == null) return;

        jdbcTemplate.update(
                "UPDATE account SET balance = balance - ? WHERE account_number = ?",
                amount, senderAccNo);

        // Simulate an error
        // int x = 10 / 0;

        // Credit receiver
        jdbcTemplate.update(
                "UPDATE account SET balance = balance + ? WHERE account_number = ?",
                amount, receiverAccNo);

                rabbitTemplate.convertAndSend("account-events","account.transfer","Money Transfer");

                log.info("Money transfer");


    }

    
}
