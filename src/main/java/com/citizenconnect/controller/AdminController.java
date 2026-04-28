package com.citizenconnect.controller;

import com.citizenconnect.dto.RoleUpdateRequest;
import com.citizenconnect.dto.StatsResponse;
import com.citizenconnect.dto.UserResponse;
import com.citizenconnect.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateUserRole(id, request.getRole()));
    }
}
