package com.campusskills.repository;

import com.campusskills.entity.AnswerUpvote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerUpvoteRepository extends JpaRepository<AnswerUpvote, Long> {
    Optional<AnswerUpvote> findByAnswerIdAndUserId(Long answerId, Long userId);
    boolean existsByAnswerIdAndUserId(Long answerId, Long userId);
    void deleteByAnswerIdAndUserId(Long answerId, Long userId);
}
