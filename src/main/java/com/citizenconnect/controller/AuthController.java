package com.citizenconnect.controller;

import com.citizenconnect.dto.*;
import com.citizenconnect.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<AuthResponse> resendOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        return ResponseEntity.ok(authService.resendOtp(email));
    }
}
