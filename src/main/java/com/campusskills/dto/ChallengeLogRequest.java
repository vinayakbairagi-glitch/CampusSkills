package com.campusskills.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ChallengeLogRequest {
    @NotBlank(message = "Please select a coding platform")
    @Size(max = 50, message = "Platform name is too long")
    private String platform = "LeetCode";

    @NotBlank(message = "Problem title or topic is required")
    @Size(max = 180, message = "Problem title must be 180 characters or fewer")
    private String problemTitle;

    @NotNull(message = "Problems solved count is required")
    @Min(value = 1, message = "Count must be at least 1")
    @Max(value = 20, message = "You can log at most 20 problems at a time")
    private Integer problemCount = 1;

    public ChallengeLogRequest() {}

    public ChallengeLogRequest(String platform, String problemTitle, Integer problemCount) {
        this.platform = platform;
        this.problemTitle = problemTitle;
        this.problemCount = problemCount;
    }

    public String getPlatform() {
        return this.platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getProblemTitle() {
        return this.problemTitle;
    }

    public void setProblemTitle(String problemTitle) {
        this.problemTitle = problemTitle;
    }

    public Integer getProblemCount() {
        return this.problemCount;
    }

    public void setProblemCount(Integer problemCount) {
        this.problemCount = problemCount;
    }
}
