package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_challenges")
public class DailyChallenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDate challengeDate;

    private Integer problemCount = 1;

    private Integer xpEarned = 5;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (challengeDate == null) challengeDate = LocalDate.now();
        if (problemCount == null) problemCount = 1;
        if (xpEarned == null) xpEarned = 5;
    }

    public DailyChallenge() {}

    public DailyChallenge(Long id, Long userId, LocalDate challengeDate, Integer problemCount, Integer xpEarned, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.challengeDate = challengeDate;
        this.problemCount = problemCount;
        this.xpEarned = xpEarned;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getChallengeDate() {
        return this.challengeDate;
    }

    public void setChallengeDate(LocalDate challengeDate) {
        this.challengeDate = challengeDate;
    }

    public Integer getProblemCount() {
        return this.problemCount;
    }

    public void setProblemCount(Integer problemCount) {
        this.problemCount = problemCount;
    }

    public Integer getXpEarned() {
        return this.xpEarned;
    }

    public void setXpEarned(Integer xpEarned) {
        this.xpEarned = xpEarned;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static DailyChallengeBuilder builder() {
        return new DailyChallengeBuilder();
    }

    public static class DailyChallengeBuilder {
        private Long id;
        private Long userId;
        private LocalDate challengeDate;
        private Integer problemCount = 1;
        private Integer xpEarned = 5;
        private LocalDateTime createdAt;

        public DailyChallengeBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DailyChallengeBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public DailyChallengeBuilder challengeDate(LocalDate challengeDate) {
            this.challengeDate = challengeDate;
            return this;
        }

        public DailyChallengeBuilder problemCount(Integer problemCount) {
            this.problemCount = problemCount;
            return this;
        }

        public DailyChallengeBuilder xpEarned(Integer xpEarned) {
            this.xpEarned = xpEarned;
            return this;
        }

        public DailyChallengeBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DailyChallenge build() {
            return new DailyChallenge(this.id, this.userId, this.challengeDate, this.problemCount, this.xpEarned, this.createdAt);
        }
    }

}
