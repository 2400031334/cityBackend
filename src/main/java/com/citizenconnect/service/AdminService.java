package com.citizenconnect.service;

import com.citizenconnect.dto.StatsResponse;
import com.citizenconnect.dto.UserResponse;
import com.citizenconnect.entity.User;
import com.citizenconnect.enums.ComplaintStatus;
import com.citizenconnect.enums.Role;
import com.citizenconnect.repository.ComplaintRepository;
import com.citizenconnect.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;

    public AdminService(UserRepository userRepository, ComplaintRepository complaintRepository) {
        this.userRepository = userRepository;
        this.complaintRepository = complaintRepository;
    }

    public StatsResponse getStats() {
        long totalComplaints = complaintRepository.count();
        long resolved = complaintRepository.countByStatus(ComplaintStatus.RESOLVED);
        double resolutionRate = totalComplaints > 0 ? (double) resolved / totalComplaints * 100 : 0;

        return StatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalCitizens(userRepository.countByRole(Role.CITIZEN))
                .totalModerators(userRepository.countByRole(Role.MODERATOR))
                .totalPoliticians(userRepository.countByRole(Role.POLITICIAN))
                .totalComplaints(totalComplaints)
                .pendingComplaints(complaintRepository.countByStatus(ComplaintStatus.PENDING))
                .verifiedComplaints(complaintRepository.countByStatus(ComplaintStatus.VERIFIED))
                .rejectedComplaints(complaintRepository.countByStatus(ComplaintStatus.REJECTED))
                .inProgressComplaints(complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS))
                .resolvedComplaints(resolved)
                .resolutionRate(Math.round(resolutionRate * 100.0) / 100.0)
                .build();
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Role role;
        try {
            role = Role.valueOf(newRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + newRole);
        }

        user.setRole(role);
        userRepository.save(user);
        return toResponse(user);
    }

    public List<UserResponse> getPoliticians() {
        return userRepository.findByRole(Role.POLITICIAN).stream().map(this::toResponse).toList();
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .phone(user.getPhone())
                .verified(user.isVerified())
                .build();
    }
}
