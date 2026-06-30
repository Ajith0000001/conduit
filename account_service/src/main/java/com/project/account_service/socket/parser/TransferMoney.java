package com.project.conduit.socket.parser;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransferMoney {
    
    private String sender;
    private String receiver;
    private int amount;
}
