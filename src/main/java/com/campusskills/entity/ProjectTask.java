package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_tasks")
public class ProjectTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long projectId;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    private Long assigneeId;
    private LocalDate deadline;

    private Boolean isCompleted = false;

    private LocalDateTime completedAt;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (isCompleted == null) isCompleted = false;
    }

    public ProjectTask() {}

    public ProjectTask(Long id, Long projectId, String title, String description, Long assigneeId, LocalDate deadline, Boolean isCompleted, LocalDateTime completedAt, LocalDateTime createdAt) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.assigneeId = assigneeId;
        this.deadline = deadline;
        this.isCompleted = isCompleted;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public Boolean getIsCompleted() {
        return this.isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public LocalDateTime getCompletedAt() {
        return this.completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static ProjectTaskBuilder builder() {
        return new ProjectTaskBuilder();
    }

    public static class ProjectTaskBuilder {
        private Long id;
        private Long projectId;
        private String title;
        private String description;
        private Long assigneeId;
        private LocalDate deadline;
        private Boolean isCompleted = false;
        private LocalDateTime completedAt;
        private LocalDateTime createdAt;

        public ProjectTaskBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ProjectTaskBuilder projectId(Long projectId) {
            this.projectId = projectId;
            return this;
        }

        public ProjectTaskBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ProjectTaskBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ProjectTaskBuilder assigneeId(Long assigneeId) {
            this.assigneeId = assigneeId;
            return this;
        }

        public ProjectTaskBuilder deadline(LocalDate deadline) {
            this.deadline = deadline;
            return this;
        }

        public ProjectTaskBuilder isCompleted(Boolean isCompleted) {
            this.isCompleted = isCompleted;
            return this;
        }

        public ProjectTaskBuilder completedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public ProjectTaskBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ProjectTask build() {
            return new ProjectTask(this.id, this.projectId, this.title, this.description, this.assigneeId, this.deadline, this.isCompleted, this.completedAt, this.createdAt);
        }
    }

}
