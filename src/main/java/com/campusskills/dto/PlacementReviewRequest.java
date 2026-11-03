package com.campusskills.dto;

import jakarta.validation.constraints.NotNull;
public class PlacementReviewRequest {
    @NotNull(message = "Student ID is required")
    private Long studentId;

    private Double resumeScore; // 0 - 100
    private Double mockInterviewRating; // 1 - 5

    public PlacementReviewRequest() {}

    public PlacementReviewRequest(Long studentId, Double resumeScore, Double mockInterviewRating) {
        this.studentId = studentId;
        this.resumeScore = resumeScore;
        this.mockInterviewRating = mockInterviewRating;
    }

    public Long getStudentId() {
        return this.studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Double getResumeScore() {
        return this.resumeScore;
    }

    public void setResumeScore(Double resumeScore) {
        this.resumeScore = resumeScore;
    }

    public Double getMockInterviewRating() {
        return this.mockInterviewRating;
    }

    public void setMockInterviewRating(Double mockInterviewRating) {
        this.mockInterviewRating = mockInterviewRating;
    }

}
