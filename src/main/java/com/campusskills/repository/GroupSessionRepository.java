package com.campusskills.repository;

import com.campusskills.entity.GroupSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface GroupSessionRepository extends JpaRepository<GroupSession, Long> {
    List<GroupSession> findByGroupIdOrderByScheduledTimeAsc(Long groupId);
    List<GroupSession> findAllByOrderByScheduledTimeAsc();
    List<GroupSession> findByScheduledTimeBetween(LocalDateTime start, LocalDateTime end);
}
