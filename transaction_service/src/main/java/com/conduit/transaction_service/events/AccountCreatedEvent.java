package com.conduit.transaction_service.events;



import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountCreatedEvent {
    
    private long accountId;
    private String accountNumber;
    private BigDecimal balance;
    private boolean blocked;
}
