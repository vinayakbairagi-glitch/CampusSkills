package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tests")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String subject;

    private String topic;

    @Column(nullable = false)
    private String testType; // PRACTICE, GROUP, MOCK, SKILL_VERIFICATION, ADAPTIVE

    private Long groupId;
    private Long skillId;

    private Integer durationMinutes = 30;

    private LocalDateTime windowStart;
    private LocalDateTime windowEnd;

    private Integer maxAttempts = 1;

    private Boolean negativeMarking = false;

    private Double passingMarks = 40.0; // percentage or minimum score

    private Boolean isOfficial = false;

    private Long bankId;

    @Column(length = 3000)
    private String selectedQuestionIdsJson; // JSON array of question IDs if picked manually/randomly

    private Long createdBy;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (durationMinutes == null) durationMinutes = 30;
        if (maxAttempts == null) maxAttempts = 1;
        if (negativeMarking == null) negativeMarking = false;
        if (passingMarks == null) passingMarks = 40.0;
        if (isOfficial == null) isOfficial = false;
    }

    public Test() {}

    public Test(Long id, String title, String description, String subject, String topic, String testType, Long groupId, Long skillId, Integer durationMinutes, LocalDateTime windowStart, LocalDateTime windowEnd, Integer maxAttempts, Boolean negativeMarking, Double passingMarks, Boolean isOfficial, Long bankId, String selectedQuestionIdsJson, Long createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.topic = topic;
        this.testType = testType;
        this.groupId = groupId;
        this.skillId = skillId;
        this.durationMinutes = durationMinutes;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.maxAttempts = maxAttempts;
        this.negativeMarking = negativeMarking;
        this.passingMarks = passingMarks;
        this.isOfficial = isOfficial;
        this.bankId = bankId;
        this.selectedQuestionIdsJson = selectedQuestionIdsJson;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTestType() {
        return this.testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public Long getGroupId() {
        return this.groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getSkillId() {
        return this.skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public Integer getDurationMinutes() {
        return this.durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public LocalDateTime getWindowStart() {
        return this.windowStart;
    }

    public void setWindowStart(LocalDateTime windowStart) {
        this.windowStart = windowStart;
    }

    public LocalDateTime getWindowEnd() {
        return this.windowEnd;
    }

    public void setWindowEnd(LocalDateTime windowEnd) {
        this.windowEnd = windowEnd;
    }

    public Integer getMaxAttempts() {
        return this.maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Boolean getNegativeMarking() {
        return this.negativeMarking;
    }

    public void setNegativeMarking(Boolean negativeMarking) {
        this.negativeMarking = negativeMarking;
    }

    public Double getPassingMarks() {
        return this.passingMarks;
    }

    public void setPassingMarks(Double passingMarks) {
        this.passingMarks = passingMarks;
    }

    public Boolean getIsOfficial() {
        return this.isOfficial;
    }

    public void setIsOfficial(Boolean isOfficial) {
        this.isOfficial = isOfficial;
    }

    public Long getBankId() {
        return this.bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getSelectedQuestionIdsJson() {
        return this.selectedQuestionIdsJson;
    }

    public void setSelectedQuestionIdsJson(String selectedQuestionIdsJson) {
        this.selectedQuestionIdsJson = selectedQuestionIdsJson;
    }

    public Long getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static TestBuilder builder() {
        return new TestBuilder();
    }

    public static class TestBuilder {
        private Long id;
        private String title;
        private String description;
        private String subject;
        private String topic;
        private String testType;
        private Long groupId;
        private Long skillId;
        private Integer durationMinutes = 30;
        private LocalDateTime windowStart;
        private LocalDateTime windowEnd;
        private Integer maxAttempts = 1;
        private Boolean negativeMarking = false;
        private Double passingMarks = 40.0;
        private Boolean isOfficial = false;
        private Long bankId;
        private String selectedQuestionIdsJson;
        private Long createdBy;
        private LocalDateTime createdAt;

        public TestBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TestBuilder title(String title) {
            this.title = title;
            return this;
        }

        public TestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public TestBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public TestBuilder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public TestBuilder testType(String testType) {
            this.testType = testType;
            return this;
        }

        public TestBuilder groupId(Long groupId) {
            this.groupId = groupId;
            return this;
        }

        public TestBuilder skillId(Long skillId) {
            this.skillId = skillId;
            return this;
        }

        public TestBuilder durationMinutes(Integer durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public TestBuilder windowStart(LocalDateTime windowStart) {
            this.windowStart = windowStart;
            return this;
        }

        public TestBuilder windowEnd(LocalDateTime windowEnd) {
            this.windowEnd = windowEnd;
            return this;
        }

        public TestBuilder maxAttempts(Integer maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }

        public TestBuilder negativeMarking(Boolean negativeMarking) {
            this.negativeMarking = negativeMarking;
            return this;
        }

        public TestBuilder passingMarks(Double passingMarks) {
            this.passingMarks = passingMarks;
            return this;
        }

        public TestBuilder isOfficial(Boolean isOfficial) {
            this.isOfficial = isOfficial;
            return this;
        }

        public TestBuilder bankId(Long bankId) {
            this.bankId = bankId;
            return this;
        }

        public TestBuilder selectedQuestionIdsJson(String selectedQuestionIdsJson) {
            this.selectedQuestionIdsJson = selectedQuestionIdsJson;
            return this;
        }

        public TestBuilder createdBy(Long createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public TestBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Test build() {
            return new Test(this.id, this.title, this.description, this.subject, this.topic, this.testType, this.groupId, this.skillId, this.durationMinutes, this.windowStart, this.windowEnd, this.maxAttempts, this.negativeMarking, this.passingMarks, this.isOfficial, this.bankId, this.selectedQuestionIdsJson, this.createdBy, this.createdAt);
        }
    }

}
