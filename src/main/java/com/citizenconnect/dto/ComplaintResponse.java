package com.citizenconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String status;
    private String citizenName;
    private String citizenEmail;
    private Long citizenId;
    private String assignedPoliticianName;
    private Long assignedPoliticianId;
    private String resolutionNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
