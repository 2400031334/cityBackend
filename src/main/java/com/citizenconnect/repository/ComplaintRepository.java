package com.citizenconnect.repository;

import com.citizenconnect.entity.Complaint;
import com.citizenconnect.entity.User;
import com.citizenconnect.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByCitizenOrderByCreatedAtDesc(User citizen);
    List<Complaint> findByStatusOrderByCreatedAtDesc(ComplaintStatus status);
    List<Complaint> findByAssignedPoliticianAndStatusInOrderByUpdatedAtDesc(User politician, List<ComplaintStatus> statuses);
    long countByStatus(ComplaintStatus status);
}
