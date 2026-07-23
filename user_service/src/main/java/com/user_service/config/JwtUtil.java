package com.user_service.config;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    final private long expiration = 1000 * 60 * 1;
    final private Key sKey = Keys.hmacShaKeyFor("mysecretkeymysecretkeymysecretkeymysecretke".getBytes());

    public String accessToken(String email) {
        return Jwts.builder()
                    .subject(email)
                    .claim("ROLE", "user")
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(sKey)
                    .id(UUID.randomUUID().toString())
                    .compact();
    }

    public String refreshToken(String email) {
        return Jwts.builder()
                    .subject(email)
                    .claim("type", "refresh")
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expiration * 3))
                    .signWith(sKey)
                    .id(UUID.randomUUID().toString())
                    .compact();
    }

    public Claims getEmail(String token) {
        return Jwts.parser()
                    .verifyWith((SecretKey)sKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
    }
    
}
