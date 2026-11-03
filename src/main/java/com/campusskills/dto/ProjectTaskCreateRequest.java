package com.campusskills.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class ProjectTaskCreateRequest {
    @NotBlank(message = "Task title is required")
    private String title;

    private String description;
    private Long assigneeId;
    private LocalDate deadline;

    public ProjectTaskCreateRequest() {}

    public ProjectTaskCreateRequest(String title, String description, Long assigneeId, LocalDate deadline) {
        this.title = title;
        this.description = description;
        this.assigneeId = assigneeId;
        this.deadline = deadline;
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

    public Long getAssigneeId() {
        return this.assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public LocalDate getDeadline() {
        return this.deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

}
