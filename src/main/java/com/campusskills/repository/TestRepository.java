package com.campusskills.repository;

import com.campusskills.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findAllByOrderByCreatedAtDesc();
    List<Test> findBySubjectIgnoreCase(String subject);
    List<Test> findByTopicIgnoreCase(String topic);
    List<Test> findByGroupId(Long groupId);
    List<Test> findBySkillId(Long skillId);
    List<Test> findByIsOfficialTrue();
    boolean existsByTitleAndSubject(String title, String subject);
}
