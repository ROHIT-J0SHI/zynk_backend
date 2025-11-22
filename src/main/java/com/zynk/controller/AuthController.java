package com.zynk.controller;

import com.zynk.dto.AuthResponse;
import com.zynk.dto.LoginRequest;
import com.zynk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request)
            .map(authResponse -> ResponseEntity.ok((Object) authResponse))
            .orElse(ResponseEntity.status(401).body("Invalid credentials"));
    }
}

