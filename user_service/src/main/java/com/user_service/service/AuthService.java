package com.user_service.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user_service.config.JwtUtil;
import com.user_service.dto.LoginDto;
import com.user_service.dto.RegisterDto;
import com.user_service.model.User;
import com.user_service.repo.UserRepo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    final private UserRepo userRepo;
    final private PasswordEncoder passwordEncoder;
    final private JwtUtil jwtUtil;

    public ResponseEntity<?> registerUser(RegisterDto registerDto) {

        log.info("register info {}",registerDto);

        
        Optional<User> optional = userRepo.getEmail(registerDto.getEmail());

        if(optional.isPresent()) {
            return ResponseEntity.status(401).body("User already exist");
        }

        User user = new User();


        String hashPassword = passwordEncoder.encode(registerDto.getPassword());

        user.setEmail(registerDto.getEmail());
        user.setPassword(hashPassword);
        user.setUserName("Ajith");

        return ResponseEntity.ok(userRepo.save(user));

    }

    public ResponseEntity<?> login(LoginDto loginDto) {

        String email = loginDto.getEmail();
        String password = loginDto.getPassword();

        Optional<User> optional = userRepo.getEmail(email);

        if(optional.isEmpty()) {
            return ResponseEntity.status(400).body("Unauthorized user");
        }

        User user = optional.get();

        boolean isMatch = passwordEncoder.matches(password, user.getPassword());

        if(!isMatch) {
            return ResponseEntity.status(400).body("Incorrect password");
        }

        String accessToken = jwtUtil.accessToken(email);
        String refreshToken = jwtUtil.refreshToken(email);

        log.info(accessToken);

        Map<String,String> res = new HashMap<>();
        res.put("accessToken", accessToken);
        res.put("refreshToken", refreshToken);


        return ResponseEntity.ok(res);

    }
    
}
