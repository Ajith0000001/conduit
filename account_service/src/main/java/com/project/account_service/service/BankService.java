package com.project.conduit.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.conduit.model.Account;
import com.project.conduit.repo.AccountRepo;
import com.project.conduit.socket.parser.AccountCreation;
import com.project.conduit.socket.parser.TransferMoney;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class BankService {

private final AccountRepo accountRepo;
    private final JdbcTemplate jdbcTemplate;


    public void accountCreation(AccountCreation accountCreation){

        if(accountCreation.getInitalAmount() == 0 ) return;

        Account account = new Account();
        account.setAccountNumber(UUID.randomUUID().toString());
        account.setBalance(BigDecimal.valueOf(accountCreation.getInitalAmount()));
        account.setBlocked(false);

        log.info("db result : {}" ,accountRepo.save(account));

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
        int x = 10 / 0;

        // Credit receiver
        jdbcTemplate.update(
                "UPDATE account SET balance = balance + ? WHERE account_number = ?",
                amount, receiverAccNo);




    }

    
}
