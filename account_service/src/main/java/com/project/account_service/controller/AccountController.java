package com.project.account_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.account_service.service.AccountService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/account")
@AllArgsConstructor
public class AccountController {

    final private AccountService accountService;


    @GetMapping("/get")
    public ResponseEntity<?> getAccount(@RequestParam Integer accountId) {
       return  accountService.getAccount(accountId);
    }
    
}
