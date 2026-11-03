package com.campusskills.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
public class ChallengeLogRequest {
    @NotNull(message = "Problems solved count is required")
    @Min(value = 1, message = "Count must be at least 1")
    private Integer problemCount = 1;

    public ChallengeLogRequest() {}

    public ChallengeLogRequest(Integer problemCount) {
        this.problemCount = problemCount;
    }

    public Integer getProblemCount() {
        return this.problemCount;
    }

    public void setProblemCount(Integer problemCount) {
        this.problemCount = problemCount;
    }

}
