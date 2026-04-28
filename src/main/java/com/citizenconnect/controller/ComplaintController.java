package com.citizenconnect.controller;

import com.citizenconnect.dto.ComplaintRequest;
import com.citizenconnect.dto.ComplaintResponse;
import com.citizenconnect.dto.StatusUpdateRequest;
import com.citizenconnect.dto.UserResponse;
import com.citizenconnect.service.AdminService;
import com.citizenconnect.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final AdminService adminService;

    public ComplaintController(ComplaintService complaintService, AdminService adminService) {
        this.complaintService = complaintService;
        this.adminService = adminService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<ComplaintResponse> fileComplaint(
            @Valid @RequestBody ComplaintRequest request,
            Authentication auth) {
        return ResponseEntity.ok(complaintService.fileComplaint(request, auth.getName()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<List<ComplaintResponse>> getMyComplaints(Authentication auth) {
        return ResponseEntity.ok(complaintService.getMyComplaints(auth.getName()));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<List<ComplaintResponse>> getPendingComplaints() {
        return ResponseEntity.ok(complaintService.getPendingComplaints());
    }

    @GetMapping("/verified")
    @PreAuthorize("hasRole('POLITICIAN')")
    public ResponseEntity<List<ComplaintResponse>> getAssignedComplaints(Authentication auth) {
        return ResponseEntity.ok(complaintService.getAssignedComplaints(auth.getName()));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MODERATOR', 'POLITICIAN')")
    public ResponseEntity<ComplaintResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request,
            Authentication auth) {
        return ResponseEntity.ok(complaintService.updateStatus(id, request, auth.getName()));
    }

    @GetMapping("/politicians")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<List<UserResponse>> getPoliticians() {
        return ResponseEntity.ok(adminService.getPoliticians());
    }
}
