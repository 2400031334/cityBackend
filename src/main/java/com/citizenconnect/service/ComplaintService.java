package com.citizenconnect.service;

import com.citizenconnect.dto.ComplaintRequest;
import com.citizenconnect.dto.ComplaintResponse;
import com.citizenconnect.dto.StatusUpdateRequest;
import com.citizenconnect.entity.Complaint;
import com.citizenconnect.entity.User;
import com.citizenconnect.enums.ComplaintCategory;
import com.citizenconnect.enums.ComplaintStatus;
import com.citizenconnect.enums.Role;
import com.citizenconnect.repository.ComplaintRepository;
import com.citizenconnect.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public ComplaintService(ComplaintRepository complaintRepository, UserRepository userRepository) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
    }

    public ComplaintResponse fileComplaint(ComplaintRequest request, String citizenEmail) {
        User citizen = userRepository.findByEmail(citizenEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ComplaintCategory category;
        try {
            category = ComplaintCategory.valueOf(request.getCategory().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category: " + request.getCategory());
        }

        Complaint complaint = Complaint.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(category)
                .status(ComplaintStatus.PENDING)
                .citizen(citizen)
                .build();
        complaintRepository.save(complaint);

        return toResponse(complaint);
    }

    public List<ComplaintResponse> getMyComplaints(String citizenEmail) {
        User citizen = userRepository.findByEmail(citizenEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return complaintRepository.findByCitizenOrderByCreatedAtDesc(citizen)
                .stream().map(this::toResponse).toList();
    }

    public List<ComplaintResponse> getPendingComplaints() {
        return complaintRepository.findByStatusOrderByCreatedAtDesc(ComplaintStatus.PENDING)
                .stream().map(this::toResponse).toList();
    }

    public List<ComplaintResponse> getAssignedComplaints(String politicianEmail) {
        User politician = userRepository.findByEmail(politicianEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<ComplaintStatus> statuses = Arrays.asList(
                ComplaintStatus.VERIFIED, ComplaintStatus.IN_PROGRESS);
        return complaintRepository.findByAssignedPoliticianAndStatusInOrderByUpdatedAtDesc(politician, statuses)
                .stream().map(this::toResponse).toList();
    }

    public ComplaintResponse updateStatus(Long complaintId, StatusUpdateRequest request, String userEmail) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ComplaintStatus newStatus;
        try {
            newStatus = ComplaintStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + request.getStatus());
        }

        // Moderator can VERIFY or REJECT pending complaints
        if (currentUser.getRole() == Role.MODERATOR) {
            if (complaint.getStatus() != ComplaintStatus.PENDING) {
                throw new IllegalStateException("Only pending complaints can be moderated");
            }
            if (newStatus != ComplaintStatus.VERIFIED && newStatus != ComplaintStatus.REJECTED) {
                throw new IllegalArgumentException("Moderator can only verify or reject complaints");
            }
            if (newStatus == ComplaintStatus.VERIFIED) {
                if (request.getAssignedPoliticianId() == null) {
                    throw new IllegalArgumentException("Must assign a politician when verifying");
                }
                User politician = userRepository.findById(request.getAssignedPoliticianId())
                        .orElseThrow(() -> new IllegalArgumentException("Politician not found"));
                if (politician.getRole() != Role.POLITICIAN) {
                    throw new IllegalArgumentException("Assigned user must be a politician");
                }
                complaint.setAssignedPolitician(politician);
            }
        }
        // Politician can move to IN_PROGRESS or RESOLVED
        else if (currentUser.getRole() == Role.POLITICIAN) {
            if (complaint.getAssignedPolitician() == null ||
                    !complaint.getAssignedPolitician().getId().equals(currentUser.getId())) {
                throw new SecurityException("This complaint is not assigned to you");
            }
            if (newStatus != ComplaintStatus.IN_PROGRESS && newStatus != ComplaintStatus.RESOLVED) {
                throw new IllegalArgumentException("Politician can only set In Progress or Resolved");
            }
            if (request.getResolutionNotes() != null) {
                complaint.setResolutionNotes(request.getResolutionNotes());
            }
        } else {
            throw new SecurityException("You don't have permission to update complaint status");
        }

        complaint.setStatus(newStatus);
        complaintRepository.save(complaint);

        return toResponse(complaint);
    }

    public List<ComplaintResponse> getAllComplaints() {
        return complaintRepository.findAll().stream().map(this::toResponse).toList();
    }

    private ComplaintResponse toResponse(Complaint c) {
        return ComplaintResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .category(c.getCategory().name())
                .status(c.getStatus().name())
                .citizenName(c.getCitizen().getName())
                .citizenEmail(c.getCitizen().getEmail())
                .citizenId(c.getCitizen().getId())
                .assignedPoliticianName(c.getAssignedPolitician() != null ? c.getAssignedPolitician().getName() : null)
                .assignedPoliticianId(c.getAssignedPolitician() != null ? c.getAssignedPolitician().getId() : null)
                .resolutionNotes(c.getResolutionNotes())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
