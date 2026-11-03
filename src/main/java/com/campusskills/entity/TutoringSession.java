package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tutoring_sessions")
public class TutoringSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long swapRequestId;

    @Column(nullable = false)
    private Long tutorId;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long skillId;

    private String skillName;

    private LocalDateTime scheduledTime;

    private Integer durationMinutes = 60;

    @Column(nullable = false)
    private String mode = "ONLINE"; // ONLINE, OFFLINE

    private String meetingLink;
    private String location;

    @Column(nullable = false)
    private String status = "SCHEDULED"; // SCHEDULED, COMPLETED, CANCELLED

    private Integer creditsCharged = 1;

    @Column(length = 1000)
    private String notes;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = "SCHEDULED";
        if (durationMinutes == null) durationMinutes = 60;
        if (creditsCharged == null) creditsCharged = 1;
        if (mode == null) mode = "ONLINE";
    }

    public TutoringSession() {}

    public TutoringSession(Long id, Long swapRequestId, Long tutorId, Long studentId, Long skillId, String skillName, LocalDateTime scheduledTime, Integer durationMinutes, String mode, String meetingLink, String location, String status, Integer creditsCharged, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.swapRequestId = swapRequestId;
        this.tutorId = tutorId;
        this.studentId = studentId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.scheduledTime = scheduledTime;
        this.durationMinutes = durationMinutes;
        this.mode = mode;
        this.meetingLink = meetingLink;
        this.location = location;
        this.status = status;
        this.creditsCharged = creditsCharged;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getSkillName() {
        return this.skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
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

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCreditsCharged() {
        return this.creditsCharged;
    }

    public void setCreditsCharged(Integer creditsCharged) {
        this.creditsCharged = creditsCharged;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static TutoringSessionBuilder builder() {
        return new TutoringSessionBuilder();
    }

    public static class TutoringSessionBuilder {
        private Long id;
        private Long swapRequestId;
        private Long tutorId;
        private Long studentId;
        private Long skillId;
        private String skillName;
        private LocalDateTime scheduledTime;
        private Integer durationMinutes = 60;
        private String mode = "ONLINE";
        private String meetingLink;
        private String location;
        private String status = "SCHEDULED";
        private Integer creditsCharged = 1;
        private String notes;
        private LocalDateTime createdAt;

        public TutoringSessionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TutoringSessionBuilder swapRequestId(Long swapRequestId) {
            this.swapRequestId = swapRequestId;
            return this;
        }

        public TutoringSessionBuilder tutorId(Long tutorId) {
            this.tutorId = tutorId;
            return this;
        }

        public TutoringSessionBuilder studentId(Long studentId) {
            this.studentId = studentId;
            return this;
        }

        public TutoringSessionBuilder skillId(Long skillId) {
            this.skillId = skillId;
            return this;
        }

        public TutoringSessionBuilder skillName(String skillName) {
            this.skillName = skillName;
            return this;
        }

        public TutoringSessionBuilder scheduledTime(LocalDateTime scheduledTime) {
            this.scheduledTime = scheduledTime;
            return this;
        }

        public TutoringSessionBuilder durationMinutes(Integer durationMinutes) {
            this.durationMinutes = durationMinutes;
            return this;
        }

        public TutoringSessionBuilder mode(String mode) {
            this.mode = mode;
            return this;
        }

        public TutoringSessionBuilder meetingLink(String meetingLink) {
            this.meetingLink = meetingLink;
            return this;
        }

        public TutoringSessionBuilder location(String location) {
            this.location = location;
            return this;
        }

        public TutoringSessionBuilder status(String status) {
            this.status = status;
            return this;
        }

        public TutoringSessionBuilder creditsCharged(Integer creditsCharged) {
            this.creditsCharged = creditsCharged;
            return this;
        }

        public TutoringSessionBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public TutoringSessionBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public TutoringSession build() {
            return new TutoringSession(this.id, this.swapRequestId, this.tutorId, this.studentId, this.skillId, this.skillName, this.scheduledTime, this.durationMinutes, this.mode, this.meetingLink, this.location, this.status, this.creditsCharged, this.notes, this.createdAt);
        }
    }

}
