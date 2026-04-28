package com.citizenconnect.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String toEmail, String otpCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Your Citizen Connect Verification Code");
            message.setText("Welcome to Citizen Connect!\n\n" +
                    "Your OTP verification code is: " + otpCode + "\n\n" +
                    "This code will expire in 5 minutes.\n\n" +
                    "Thank you,\nThe Citizen Connect Team");

            mailSender.send(message);
            log.info("Actual OTP email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send real OTP email to {}", toEmail, e);
            
            // Fallback to console simulation so development isn't completely blocked if SMTP fails
            log.info("═══════════════════════════════════════════");
            log.info("  OTP EMAIL SIMULATION (Fallback)");
            log.info("  To: {}", toEmail);
            log.info("  OTP Code: {}", otpCode);
            log.info("  Expires in: 5 minutes");
            log.info("═══════════════════════════════════════════");
        }
    }
}
