package com.campusskills.repository;

import com.campusskills.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProjectId(Long projectId);
    List<ProjectMember> findByUserId(Long userId);
    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);
    long countByProjectIdAndStatus(Long projectId, String status);
    boolean existsByProjectIdAndUserIdAndStatus(Long projectId, Long userId, String status);
}
