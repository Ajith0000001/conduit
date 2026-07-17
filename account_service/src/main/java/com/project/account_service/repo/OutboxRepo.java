package com.project.account_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.account_service.model.OutboxEvent;

@Repository
public interface OutboxRepo extends JpaRepository<OutboxEvent,Integer>{

    
}