package com.conduit.transaction_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.conduit.transaction_service.model.AuditLog;

@Repository
public interface AuditLogRepo extends JpaRepository<AuditLog,Integer>{

    

}