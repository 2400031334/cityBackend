package com.citizenconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status;

    private Long assignedPoliticianId;

    private String resolutionNotes;
}
