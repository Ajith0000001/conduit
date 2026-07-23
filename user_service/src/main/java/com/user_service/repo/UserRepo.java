package com.user_service.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.user_service.model.User;

public interface UserRepo extends JpaRepository<User,Integer> {

    @Query("SELECT u FROM User u where email = :email")
    Optional<User> getEmail(String email);
    
}
