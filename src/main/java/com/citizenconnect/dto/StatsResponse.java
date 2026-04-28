package com.citizenconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponse {
    private long totalUsers;
    private long totalCitizens;
    private long totalModerators;
    private long totalPoliticians;
    private long totalComplaints;
    private long pendingComplaints;
    private long verifiedComplaints;
    private long rejectedComplaints;
    private long inProgressComplaints;
    private long resolvedComplaints;
    private double resolutionRate;
}
