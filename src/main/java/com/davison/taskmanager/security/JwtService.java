package com.davison.taskmanager.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class JwtService {

    @Value("${JWT_SECRET}")
    private String secretKey;

    private static final long EXPIRATION_TIME = 86400000; // 24 horas

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secretKey);
    }

    public String generateToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(getAlgorithm());
    }

    public String extractUsername(String token) {
        DecodedJWT decoded = JWT.require(getAlgorithm()).build().verify(token);
        return decoded.getSubject();
    }

    public boolean isTokenValid(String token, String email) {
        try {
            String username = extractUsername(token);
            return username.equals(email) && !isTokenExpired(token);
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        DecodedJWT decoded = JWT.require(getAlgorithm()).build().verify(token);
        return decoded.getExpiresAt().before(new Date());
    }
}