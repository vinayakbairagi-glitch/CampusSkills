package com.campusskills.dto;

import jakarta.validation.constraints.NotBlank;
public class StudyGroupCreateRequest {
    @NotBlank(message = "Group name is required")
    private String name;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Category is required (SUBJECT, EXAM, SKILL, PLACEMENT)")
    private String category;

    private String branch;
    private Integer studyYear;
    private String timing;
    private String mode = "ONLINE"; // ONLINE, OFFLINE, HYBRID
    private Integer memberLimit = 30;
    private String visibility = "PUBLIC"; // PUBLIC, PRIVATE
    private String description;

    public StudyGroupCreateRequest() {}

    public StudyGroupCreateRequest(String name, String subject, String category, String branch, Integer studyYear, String timing, String mode, Integer memberLimit, String visibility, String description) {
        this.name = name;
        this.subject = subject;
        this.category = category;
        this.branch = branch;
        this.studyYear = studyYear;
        this.timing = timing;
        this.mode = mode;
        this.memberLimit = memberLimit;
        this.visibility = visibility;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBranch() {
        return this.branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Integer getStudyYear() {
        return this.studyYear;
    }

    public void setStudyYear(Integer studyYear) {
        this.studyYear = studyYear;
    }

    public String getTiming() {
        return this.timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getMemberLimit() {
        return this.memberLimit;
    }

    public void setMemberLimit(Integer memberLimit) {
        this.memberLimit = memberLimit;
    }

    public String getVisibility() {
        return this.visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
