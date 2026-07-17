package com.project.dtos;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountCreationEvent {
    
    private long accountId;
    private String accountNumber;
    private BigDecimal balance;
    private boolean blocked;
    private String eventId;
}

