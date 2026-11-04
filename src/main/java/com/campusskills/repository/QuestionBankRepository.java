package com.campusskills.repository;

import com.campusskills.entity.QuestionBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {
    List<QuestionBank> findAllByOrderByCreatedAtDesc();
    List<QuestionBank> findBySubjectIgnoreCase(String subject);
    java.util.Optional<QuestionBank> findFirstByTitleAndSubjectAndTopic(String title, String subject, String topic);
}
