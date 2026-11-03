package com.campusskills.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public class TestCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Subject is required")
    private String subject;

    private String topic;

    @NotBlank(message = "Test type is required (PRACTICE, GROUP, MOCK, SKILL_VERIFICATION, ADAPTIVE)")
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
    private List<Long> questionIds; // optional specific selection

    public TestCreateRequest() {}

    public TestCreateRequest(String title, String description, String subject, String topic, String testType, Long groupId, Long skillId, Integer durationMinutes, LocalDateTime windowStart, LocalDateTime windowEnd, Integer maxAttempts, Boolean negativeMarking, Double passingMarks, Boolean isOfficial, Long bankId, List<Long> questionIds) {
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
        this.questionIds = questionIds;
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

    public List<Long> getQuestionIds() {
        return this.questionIds;
    }

    public void setQuestionIds(List<Long> questionIds) {
        this.questionIds = questionIds;
    }

}
