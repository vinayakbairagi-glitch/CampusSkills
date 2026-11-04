package com.campusskills.repository;

import com.campusskills.entity.TestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {
    List<TestAttempt> findByTestIdOrderByScoreDescTimeTakenSecondsAsc(Long testId);
    List<TestAttempt> findByUserIdOrderByStartTimeDesc(Long userId);
    List<TestAttempt> findByTestIdAndUserIdOrderByAttemptNumberDesc(Long testId, Long userId);
    Optional<TestAttempt> findFirstByTestIdAndUserIdOrderByScoreDesc(Long testId, Long userId);
    long countByTestIdAndUserId(Long testId, Long userId);
    long countByUserId(Long userId);
    boolean existsByUserIdAndPercentageGreaterThanEqual(Long userId, Double percentage);
}
