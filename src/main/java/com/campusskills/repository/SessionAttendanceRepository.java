package com.campusskills.repository;

import com.campusskills.entity.SessionAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionAttendanceRepository extends JpaRepository<SessionAttendance, Long> {
    List<SessionAttendance> findByGroupSessionId(Long groupSessionId);
    Optional<SessionAttendance> findByGroupSessionIdAndUserId(Long groupSessionId, Long userId);
    List<SessionAttendance> findByUserId(Long userId);
    long countByUserIdAndCheckedInTrue(Long userId);
    long countByUserId(Long userId);
}
