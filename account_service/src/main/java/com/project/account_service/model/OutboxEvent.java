package com.project.account_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "outbox")
@Data
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String event_id;
    private String eventType;
    private String correlationId;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private String status; // PENDING, SENT

    private LocalDateTime createdAt;
}