package com.campusskills.repository;

import com.campusskills.entity.TutoringSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface TutoringSessionRepository extends JpaRepository<TutoringSession, Long> {
    List<TutoringSession> findByTutorIdOrStudentIdOrderByScheduledTimeDesc(Long tutorId, Long studentId);
    List<TutoringSession> findByTutorIdAndStatus(Long tutorId, String status);
    List<TutoringSession> findByStudentIdAndStatus(Long studentId, String status);
    List<TutoringSession> findByStatusAndScheduledTimeBetween(String status, LocalDateTime start, LocalDateTime end);
    long countByStatus(String status);
}
