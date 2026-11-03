package com.campusskills.repository;

import com.campusskills.entity.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {
    List<ProjectTask> findByProjectIdOrderByCreatedAtAsc(Long projectId);
    long countByProjectId(Long projectId);
    long countByProjectIdAndIsCompletedTrue(Long projectId);
}
