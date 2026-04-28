package com.citizenconnect.service;

import com.citizenconnect.entity.Otp;
import com.citizenconnect.repository.OtpRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private final Random random = new Random();

    public OtpService(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    public String generateOtp(String email) {
        String code = String.format("%06d", random.nextInt(999999));

        Otp otp = Otp.builder()
                .email(email)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();
        otpRepository.save(otp);

        return code;
    }

    public boolean verifyOtp(String email, String code) {
        return otpRepository.findTopByEmailAndUsedFalseOrderByExpiresAtDesc(email)
                .filter(otp -> otp.getCode().equals(code))
                .filter(otp -> otp.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(otp -> {
                    otp.setUsed(true);
                    otpRepository.save(otp);
                    return true;
                })
                .orElse(false);
    }
}
