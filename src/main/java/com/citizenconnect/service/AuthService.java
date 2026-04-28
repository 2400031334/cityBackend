package com.citizenconnect.service;

import com.citizenconnect.dto.*;
import com.citizenconnect.entity.User;
import com.citizenconnect.enums.Role;
import com.citizenconnect.repository.UserRepository;
import com.citizenconnect.security.JwtUtil;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, OtpService otpService, EmailService emailService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.otpService = otpService;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already registered");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + request.getRole());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .phone(request.getPhone())
                .verified(false)
                .build();
        userRepository.save(user);

        // Generate and send OTP
        String otp = otpService.generateOtp(user.getEmail());
        emailService.sendOtpEmail(user.getEmail(), otp);

        return AuthResponse.builder()
                .message("Registration successful. Please verify your email with the OTP sent.")
                .user(toUserResponse(user))
                .build();
    }

    public AuthResponse verifyOtp(OtpVerifyRequest request) {
        boolean valid = otpService.verifyOtp(request.getEmail(), request.getCode());
        if (!valid) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setVerified(true);
        userRepository.save(user);

        // Generate token after verification
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .message("Email verified successfully")
                .user(toUserResponse(user))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.isVerified()) {
            // Re-send OTP
            String otp = otpService.generateOtp(user.getEmail());
            emailService.sendOtpEmail(user.getEmail(), otp);
            throw new DisabledException("Account not verified. A new OTP has been sent to your email.");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .message("Login successful")
                .user(toUserResponse(user))
                .build();
    }

    public AuthResponse resendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, otp);

        return AuthResponse.builder()
                .message("OTP sent successfully")
                .user(toUserResponse(user))
                .build();
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .phone(user.getPhone())
                .verified(user.isVerified())
                .build();
    }
}
