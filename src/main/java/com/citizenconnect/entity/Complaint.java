package com.citizenconnect.entity;

import com.citizenconnect.enums.ComplaintCategory;
import com.citizenconnect.enums.ComplaintStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", nullable = false)
    private User citizen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_politician_id")
    private User assignedPolitician;

    @Column(length = 2000)
    private String resolutionNotes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ComplaintStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
