package com.campusskills.repository;

import com.campusskills.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByOrderByCreatedAtDesc();
    List<Project> findByStatus(String status);
    List<Project> findByLeaderId(Long leaderId);
}
