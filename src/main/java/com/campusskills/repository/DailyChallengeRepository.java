package com.campusskills.repository;

import com.campusskills.entity.DailyChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyChallengeRepository extends JpaRepository<DailyChallenge, Long> {
    List<DailyChallenge> findByUserIdOrderByChallengeDateDesc(Long userId);
    Optional<DailyChallenge> findByUserIdAndChallengeDate(Long userId, LocalDate challengeDate);
}
