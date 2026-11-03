package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_sessions")
public class GroupSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long groupId;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    private LocalDateTime scheduledTime;

    private Integer durationMinutes = 60;

    private String mode = "ONLINE"; // ONLINE, OFFLINE, HYBRID

    private String meetingLink;
    private String location;

    private Integer recurrenceWeeks = 0; // 0 for one-off, up to 12

    private Long createdBy;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (durationMinutes == null) durationMinutes = 60;
        if (mode == null) mode = "ONLINE";
        if (recurrenceWeeks == null) recurrenceWeeks = 0;
    }

    public GroupSession() {}

    public GroupSession(Long id, Long groupId, String title, String description, LocalDateTime scheduledTime, Integer durationMinutes, String mode, String meetingLink, String location, Integer recurrenceWeeks, Long createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.groupId = groupId;
        this.title = title;
        this.description = description;
        this.scheduledTime = scheduledTime;
        this.durationMinutes = durationMinutes;
        this.mode = mode;
        this.meetingLink = meetingLink;
        this.location = location;
        this.recurrenceWeeks = recurrenceWeeks;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return this.groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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

    public static GroupSessionBuilder builder() {
        return new GroupSessionBuilder();
    }

    public static class GroupSessionBuilder {
        private Long id;
        private Long groupId;
        private String title;
        private String description;
        private LocalDateTime scheduledTime;
        private Integer durationMinutes = 60;
        private String mode = "ONLINE";
        private String meetingLink;
        private String location;
        private Integer recurrenceWeeks = 0;
        private Long createdBy;
        private LocalDateTime createdAt;

        public GroupSessionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public GroupSessionBuilder groupId(Long groupId) {
            this.groupId = groupId;
            return this;
        }

        public GroupSessionBuilder title(String title) {
            this.title = title;
            return this;
        }

        public GroupSessionBuilder description(String description) {
            this.description = description;
            return this;
        }

        public GroupSessionBuilder scheduledTime(LocalDateTime scheduledTime) {
            this.scheduledTime = scheduledTime;
            return this;
        }

        public GroupSessionBuilder durationMinutes(Integer durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public GroupSessionBuilder mode(String mode) {
            this.mode = mode;
            return this;
        }

        public GroupSessionBuilder meetingLink(String meetingLink) {
            this.meetingLink = meetingLink;
            return this;
        }

        public GroupSessionBuilder location(String location) {
            this.location = location;
            return this;
        }

        public GroupSessionBuilder recurrenceWeeks(Integer recurrenceWeeks) {
            this.recurrenceWeeks = recurrenceWeeks;
            return this;
        }

        public GroupSessionBuilder createdBy(Long createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public GroupSessionBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public GroupSession build() {
            return new GroupSession(this.id, this.groupId, this.title, this.description, this.scheduledTime, this.durationMinutes, this.mode, this.meetingLink, this.location, this.recurrenceWeeks, this.createdBy, this.createdAt);
        }
    }

}
