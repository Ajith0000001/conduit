package com.project.account_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "audit_log")
@Data
public class AuditLog {

    @Id
    private Integer id;
    private String event_id;
    private String event;
    
}
