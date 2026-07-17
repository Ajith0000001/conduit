package com.conduit.transaction_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "transaction_history")
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    private String senderAccount;

    private String receiverAccount;

    private BigDecimal amount;

    private String transactionType; // TRANSFER, DEPOSIT, WITHDRAW

    private String status; // PENDING, SUCCESS, FAILED

    private String description;

    private String failureReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
