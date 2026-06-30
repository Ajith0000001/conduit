package com.project.conduit.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.conduit.model.Account;

public interface AccountRepo extends JpaRepository<Account,Integer>{

    @Query("SELECT a FROM Account a where a.accountNumber = :accountNo")
    Account getByAccountNo(String accountNo);
    
    
}
