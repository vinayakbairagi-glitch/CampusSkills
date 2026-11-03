package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_attempts")
public class TestAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long testId;

    @Column(nullable = false)
    private Long userId;

    private Integer attemptNumber = 1;

    private LocalDateTime startTime;
    private LocalDateTime endTime; // server-side deadline
    private LocalDateTime submittedAt;

    private Integer timeTakenSeconds;

    private Double score = 0.0;

    private Double totalMarks = 0.0;

    private Double percentage = 0.0;

    private Boolean passed = false;

    private Integer rankInTest;

    @Column(nullable = false)
    private String status = "IN_PROGRESS"; // IN_PROGRESS, SUBMITTED, AUTO_SUBMITTED

    private Integer currentQuestionIndex = 0;

    private String adaptiveDifficulty = "MEDIUM"; // For ADAPTIVE tests: EASY, MEDIUM, HARD

    @Column(length = 4000)
    private String questionOrderJson; // Ordered list of question IDs for this attempt

    @PrePersist
    protected void onCreate() {
        if (startTime == null) startTime = LocalDateTime.now();
        if (status == null) status = "IN_PROGRESS";
        if (score == null) score = 0.0;
        if (percentage == null) percentage = 0.0;
        if (passed == null) passed = false;
        if (currentQuestionIndex == null) currentQuestionIndex = 0;
        if (adaptiveDifficulty == null) adaptiveDifficulty = "MEDIUM";
    }

    public TestAttempt() {}

    public TestAttempt(Long id, Long testId, Long userId, Integer attemptNumber, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime submittedAt, Integer timeTakenSeconds, Double score, Double totalMarks, Double percentage, Boolean passed, Integer rankInTest, String status, Integer currentQuestionIndex, String adaptiveDifficulty, String questionOrderJson) {
        this.id = id;
        this.testId = testId;
        this.userId = userId;
        this.attemptNumber = attemptNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.submittedAt = submittedAt;
        this.timeTakenSeconds = timeTakenSeconds;
        this.score = score;
        this.totalMarks = totalMarks;
        this.percentage = percentage;
        this.passed = passed;
        this.rankInTest = rankInTest;
        this.status = status;
        this.currentQuestionIndex = currentQuestionIndex;
        this.adaptiveDifficulty = adaptiveDifficulty;
        this.questionOrderJson = questionOrderJson;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTestId() {
        return this.testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getAttemptNumber() {
        return this.attemptNumber;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getSubmittedAt() {
        return this.submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Integer getTimeTakenSeconds() {
        return this.timeTakenSeconds;
    }

    public void setTimeTakenSeconds(Integer timeTakenSeconds) {
        this.timeTakenSeconds = timeTakenSeconds;
    }

    public Double getScore() {
        return this.score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getTotalMarks() {
        return this.totalMarks;
    }

    public void setTotalMarks(Double totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Double getPercentage() {
        return this.percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Boolean getPassed() {
        return this.passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

    public Integer getRankInTest() {
        return this.rankInTest;
    }

    public void setRankInTest(Integer rankInTest) {
        this.rankInTest = rankInTest;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCurrentQuestionIndex() {
        return this.currentQuestionIndex;
    }

    public void setCurrentQuestionIndex(Integer currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public String getAdaptiveDifficulty() {
        return this.adaptiveDifficulty;
    }

    public void setAdaptiveDifficulty(String adaptiveDifficulty) {
        this.adaptiveDifficulty = adaptiveDifficulty;
    }

    public String getQuestionOrderJson() {
        return this.questionOrderJson;
    }

    public void setQuestionOrderJson(String questionOrderJson) {
        this.questionOrderJson = questionOrderJson;
    }

    public static TestAttemptBuilder builder() {
        return new TestAttemptBuilder();
    }

    public static class TestAttemptBuilder {
        private Long id;
        private Long testId;
        private Long userId;
        private Integer attemptNumber = 1;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private LocalDateTime submittedAt;
        private Integer timeTakenSeconds;
        private Double score = 0.0;
        private Double totalMarks = 0.0;
        private Double percentage = 0.0;
        private Boolean passed = false;
        private Integer rankInTest;
        private String status = "IN_PROGRESS";
        private Integer currentQuestionIndex = 0;
        private String adaptiveDifficulty = "MEDIUM";
        private String questionOrderJson;

        public TestAttemptBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TestAttemptBuilder testId(Long testId) {
            this.testId = testId;
            return this;
        }

        public TestAttemptBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public TestAttemptBuilder attemptNumber(Integer attemptNumber) {
            this.attemptNumber = attemptNumber;
            return this;
        }

        public TestAttemptBuilder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public TestAttemptBuilder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public TestAttemptBuilder submittedAt(LocalDateTime submittedAt) {
            this.submittedAt = submittedAt;
            return this;
        }

        public TestAttemptBuilder timeTakenSeconds(Integer timeTakenSeconds) {
            this.timeTakenSeconds = timeTakenSeconds;
            return this;
        }

        public TestAttemptBuilder score(Double score) {
            this.score = score;
            return this;
        }

        public TestAttemptBuilder totalMarks(Double totalMarks) {
            this.totalMarks = totalMarks;
            return this;
        }

        public TestAttemptBuilder percentage(Double percentage) {
            this.percentage = percentage;
            return this;
        }

        public TestAttemptBuilder passed(Boolean passed) {
            this.passed = passed;
            return this;
        }

        public TestAttemptBuilder rankInTest(Integer rankInTest) {
            this.rankInTest = rankInTest;
            return this;
        }

        public TestAttemptBuilder status(String status) {
            this.status = status;
            return this;
        }

        public TestAttemptBuilder currentQuestionIndex(Integer currentQuestionIndex) {
            this.currentQuestionIndex = currentQuestionIndex;
            return this;
        }

        public TestAttemptBuilder adaptiveDifficulty(String adaptiveDifficulty) {
            this.adaptiveDifficulty = adaptiveDifficulty;
            return this;
        }

        public TestAttemptBuilder questionOrderJson(String questionOrderJson) {
            this.questionOrderJson = questionOrderJson;
            return this;
        }

        public TestAttempt build() {
            return new TestAttempt(this.id, this.testId, this.userId, this.attemptNumber, this.startTime, this.endTime, this.submittedAt, this.timeTakenSeconds, this.score, this.totalMarks, this.percentage, this.passed, this.rankInTest, this.status, this.currentQuestionIndex, this.adaptiveDifficulty, this.questionOrderJson);
        }
    }

}
