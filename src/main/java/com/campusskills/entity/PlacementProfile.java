package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "placement_profiles")
public class PlacementProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    private Integer dsaSolvedCount = 0;

    private Double aptitudeScore = 0.0; // average best MOCK test %

    private Double mockInterviewRating = 0.0; // 0 - 5.0

    private Double resumeScore = 0.0; // 0 - 100

    private Double placementReadiness = 0.0; // 0 - 100

    private LocalDate lastChallengeDate;

    private Integer streakDays = 0;

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        updatedAt = LocalDateTime.now();
        if (dsaSolvedCount == null) dsaSolvedCount = 0;
        if (aptitudeScore == null) aptitudeScore = 0.0;
        if (mockInterviewRating == null) mockInterviewRating = 0.0;
        if (resumeScore == null) resumeScore = 0.0;
        if (streakDays == null) streakDays = 0;
    }

    public PlacementProfile() {}

    public PlacementProfile(Long id, Long userId, Integer dsaSolvedCount, Double aptitudeScore, Double mockInterviewRating, Double resumeScore, Double placementReadiness, LocalDate lastChallengeDate, Integer streakDays, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.dsaSolvedCount = dsaSolvedCount;
        this.aptitudeScore = aptitudeScore;
        this.mockInterviewRating = mockInterviewRating;
        this.resumeScore = resumeScore;
        this.placementReadiness = placementReadiness;
        this.lastChallengeDate = lastChallengeDate;
        this.streakDays = streakDays;
        this.updatedAt = updatedAt;
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

    public Integer getDsaSolvedCount() {
        return this.dsaSolvedCount;
    }

    public void setDsaSolvedCount(Integer dsaSolvedCount) {
        this.dsaSolvedCount = dsaSolvedCount;
    }

    public Double getAptitudeScore() {
        return this.aptitudeScore;
    }

    public void setAptitudeScore(Double aptitudeScore) {
        this.aptitudeScore = aptitudeScore;
    }

    public Double getMockInterviewRating() {
        return this.mockInterviewRating;
    }

    public void setMockInterviewRating(Double mockInterviewRating) {
        this.mockInterviewRating = mockInterviewRating;
    }

    public Double getResumeScore() {
        return this.resumeScore;
    }

    public void setResumeScore(Double resumeScore) {
        this.resumeScore = resumeScore;
    }

    public Double getPlacementReadiness() {
        return this.placementReadiness;
    }

    public void setPlacementReadiness(Double placementReadiness) {
        this.placementReadiness = placementReadiness;
    }

    public LocalDate getLastChallengeDate() {
        return this.lastChallengeDate;
    }

    public void setLastChallengeDate(LocalDate lastChallengeDate) {
        this.lastChallengeDate = lastChallengeDate;
    }

    public Integer getStreakDays() {
        return this.streakDays;
    }

    public void setStreakDays(Integer streakDays) {
        this.streakDays = streakDays;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static PlacementProfileBuilder builder() {
        return new PlacementProfileBuilder();
    }

    public static class PlacementProfileBuilder {
        private Long id;
        private Long userId;
        private Integer dsaSolvedCount = 0;
        private Double aptitudeScore = 0.0;
        private Double mockInterviewRating = 0.0;
        private Double resumeScore = 0.0;
        private Double placementReadiness = 0.0;
        private LocalDate lastChallengeDate;
        private Integer streakDays = 0;
        private LocalDateTime updatedAt;

        public PlacementProfileBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PlacementProfileBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public PlacementProfileBuilder dsaSolvedCount(Integer dsaSolvedCount) {
            this.dsaSolvedCount = dsaSolvedCount;
            return this;
        }

        public PlacementProfileBuilder aptitudeScore(Double aptitudeScore) {
            this.aptitudeScore = aptitudeScore;
            return this;
        }

        public PlacementProfileBuilder mockInterviewRating(Double mockInterviewRating) {
            this.mockInterviewRating = mockInterviewRating;
            return this;
        }

        public PlacementProfileBuilder resumeScore(Double resumeScore) {
            this.resumeScore = resumeScore;
            return this;
        }

        public PlacementProfileBuilder placementReadiness(Double placementReadiness) {
            this.placementReadiness = placementReadiness;
            return this;
        }

        public PlacementProfileBuilder lastChallengeDate(LocalDate lastChallengeDate) {
            this.lastChallengeDate = lastChallengeDate;
            return this;
        }

        public PlacementProfileBuilder streakDays(Integer streakDays) {
            this.streakDays = streakDays;
            return this;
        }

        public PlacementProfileBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PlacementProfile build() {
            return new PlacementProfile(this.id, this.userId, this.dsaSolvedCount, this.aptitudeScore, this.mockInterviewRating, this.resumeScore, this.placementReadiness, this.lastChallengeDate, this.streakDays, this.updatedAt);
        }
    }

}
