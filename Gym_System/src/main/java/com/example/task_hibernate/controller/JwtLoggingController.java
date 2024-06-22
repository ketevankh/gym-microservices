package com.example.task_hibernate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class JwtLoggingController {

    @GetMapping("/log-jwt")
    public ResponseEntity<String> logJwt() {
        String jwtToken = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() != null) {
            jwtToken = (String) authentication.getCredentials();
            // Log the JWT token
            log.info("JWT Token: " + jwtToken);
            System.out.println("JWT Token: " + jwtToken);
        }
        return ResponseEntity.ok("JWT token logged: " + jwtToken);
    }
}

