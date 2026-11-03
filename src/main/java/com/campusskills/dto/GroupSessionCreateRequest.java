package com.campusskills.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class GroupSessionCreateRequest {
    @NotBlank(message = "Session title is required")
    private String title;

    private String description;
    private LocalDateTime scheduledTime;
    private Integer durationMinutes = 60;
    private String mode = "ONLINE"; // ONLINE, OFFLINE, HYBRID
    private String meetingLink;
    private String location;
    private Integer recurrenceWeeks = 0; // up to 12

    public GroupSessionCreateRequest() {}

    public GroupSessionCreateRequest(String title, String description, LocalDateTime scheduledTime, Integer durationMinutes, String mode, String meetingLink, String location, Integer recurrenceWeeks) {
        this.title = title;
        this.description = description;
        this.scheduledTime = scheduledTime;
        this.durationMinutes = durationMinutes;
        this.mode = mode;
        this.meetingLink = meetingLink;
        this.location = location;
        this.recurrenceWeeks = recurrenceWeeks;
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

    public Integer getRecurrenceWeeks() {
        return this.recurrenceWeeks;
    }

    public void setRecurrenceWeeks(Integer recurrenceWeeks) {
        this.recurrenceWeeks = recurrenceWeeks;
    }

}
