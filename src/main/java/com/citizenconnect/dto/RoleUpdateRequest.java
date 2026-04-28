package com.citizenconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleUpdateRequest {

    @NotBlank(message = "Role is required")
    private String role;
}
