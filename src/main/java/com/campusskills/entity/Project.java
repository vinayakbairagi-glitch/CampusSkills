package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long leaderId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 3000)
    private String description;

    private String requiredSkills; // comma separated
    private String rolesNeeded;    // comma separated

    private Integer teamSizeCap = 4;

    private LocalDate deadline;

    @Column(nullable = false)
    private String status = "RECRUITING"; // RECRUITING, IN_PROGRESS, COMPLETED

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = "RECRUITING";
        if (teamSizeCap == null) teamSizeCap = 4;
    }

    public Project() {}

    public Project(Long id, Long leaderId, String title, String description, String requiredSkills, String rolesNeeded, Integer teamSizeCap, LocalDate deadline, String status, LocalDateTime createdAt) {
        this.id = id;
        this.leaderId = leaderId;
        this.title = title;
        this.description = description;
        this.requiredSkills = requiredSkills;
        this.rolesNeeded = rolesNeeded;
        this.teamSizeCap = teamSizeCap;
        this.deadline = deadline;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLeaderId() {
        return this.leaderId;
    }

    public void setLeaderId(Long leaderId) {
        this.leaderId = leaderId;
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

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static ProjectBuilder builder() {
        return new ProjectBuilder();
    }

    public static class ProjectBuilder {
        private Long id;
        private Long leaderId;
        private String title;
        private String description;
        private String requiredSkills;
        private String rolesNeeded;
        private Integer teamSizeCap = 4;
        private LocalDate deadline;
        private String status = "RECRUITING";
        private LocalDateTime createdAt;

        public ProjectBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ProjectBuilder leaderId(Long leaderId) {
            this.leaderId = leaderId;
            return this;
        }

        public ProjectBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ProjectBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ProjectBuilder requiredSkills(String requiredSkills) {
            this.requiredSkills = requiredSkills;
            return this;
        }

        public ProjectBuilder rolesNeeded(String rolesNeeded) {
            this.rolesNeeded = rolesNeeded;
            return this;
        }

        public ProjectBuilder teamSizeCap(Integer teamSizeCap) {
            this.teamSizeCap = teamSizeCap;
            return this;
        }

        public ProjectBuilder deadline(LocalDate deadline) {
            this.deadline = deadline;
            return this;
        }

        public ProjectBuilder status(String status) {
            this.status = status;
            return this;
        }

        public ProjectBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Project build() {
            return new Project(this.id, this.leaderId, this.title, this.description, this.requiredSkills, this.rolesNeeded, this.teamSizeCap, this.deadline, this.status, this.createdAt);
        }
    }

}
