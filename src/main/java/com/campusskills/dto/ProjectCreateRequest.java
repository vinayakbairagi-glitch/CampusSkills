package com.campusskills.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class ProjectCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String requiredSkills; // comma separated
    private String rolesNeeded;    // comma separated
    private Integer teamSizeCap = 4;
    private LocalDate deadline;

    public ProjectCreateRequest() {}

    public ProjectCreateRequest(String title, String description, String requiredSkills, String rolesNeeded, Integer teamSizeCap, LocalDate deadline) {
        this.title = title;
        this.description = description;
        this.requiredSkills = requiredSkills;
        this.rolesNeeded = rolesNeeded;
        this.teamSizeCap = teamSizeCap;
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

    public String getRequiredSkills() {
        return this.requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String getRolesNeeded() {
        return this.rolesNeeded;
    }

    public void setRolesNeeded(String rolesNeeded) {
        this.rolesNeeded = rolesNeeded;
    }

    public Integer getTeamSizeCap() {
        return this.teamSizeCap;
    }

    public void setTeamSizeCap(Integer teamSizeCap) {
        this.teamSizeCap = teamSizeCap;
    }

    public LocalDate getDeadline() {
        return this.deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

}
