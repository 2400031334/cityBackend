package com.citizenconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @PrePersist
    protected void onCreate() {
        if (this.expiresAt == null) {
            this.expiresAt = LocalDateTime.now().plusMinutes(5);
        }
    }
}
