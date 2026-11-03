package com.campusskills.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class SessionCreateRequest {
    private Long swapRequestId;

    @NotNull(message = "Tutor ID is required")
    private Long tutorId;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    private LocalDateTime scheduledTime;
    private Integer durationMinutes = 60;
    private String mode = "ONLINE"; // ONLINE, OFFLINE
    private String meetingLink;
    private String location;
    private String notes;

    public SessionCreateRequest() {}

    public SessionCreateRequest(Long swapRequestId, Long tutorId, Long studentId, Long skillId, LocalDateTime scheduledTime, Integer durationMinutes, String mode, String meetingLink, String location, String notes) {
        this.swapRequestId = swapRequestId;
        this.tutorId = tutorId;
        this.studentId = studentId;
        this.skillId = skillId;
        this.scheduledTime = scheduledTime;
        this.durationMinutes = durationMinutes;
        this.mode = mode;
        this.meetingLink = meetingLink;
        this.location = location;
        this.notes = notes;
    }

    public Long getSwapRequestId() {
        return this.swapRequestId;
    }

    public void setSwapRequestId(Long swapRequestId) {
        this.swapRequestId = swapRequestId;
    }

    public Long getTutorId() {
        return this.tutorId;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public Long getStudentId() {
        return this.studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getSkillId() {
        return this.skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public LocalDateTime getScheduledTime() {
        return this.scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Integer getDurationMinutes() {
        return this.durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMeetingLink() {
        return this.meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
