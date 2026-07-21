package com.project.account_service.service;

import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.account_service.model.Account;
import com.project.account_service.repo.AccountRepo;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {

    final private AccountRepo accountRepo;
    final private RedisTemplate<String, Object> redisTemplate;


    public ResponseEntity<?> getAccount(Integer accountId) {

       String key = String.valueOf(accountId);
       Object object =  redisTemplate.opsForValue().get(key);

       log.info("redis hit:{}",object);


       if(object != null) return ResponseEntity.ok(object);

       Optional<Account> account = accountRepo.findById(accountId);

       if(account.isPresent()) {

       }

       Account account2 = account.get();

       log.info("db hit:{}",account2);

       redisTemplate.opsForValue().set(key, account2);

        return ResponseEntity.ok(account2);

    }
    
}
