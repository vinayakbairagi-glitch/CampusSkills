package com.campusskills.repository;

import com.campusskills.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByBankId(Long bankId);
    List<Question> findBySubjectIgnoreCaseAndTopicIgnoreCase(String subject, String topic);
    List<Question> findBySubjectIgnoreCase(String subject);
    List<Question> findBySubjectIgnoreCaseAndDifficulty(String subject, String difficulty);
    List<Question> findByTopicIgnoreCaseAndDifficulty(String topic, String difficulty);
}
