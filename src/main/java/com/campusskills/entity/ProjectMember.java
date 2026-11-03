package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_members")
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long projectId;

    @Column(nullable = false)
    private Long userId;

    private String roleApplied;

    @Column(length = 1000)
    private String message;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, ACCEPTED, REJECTED

    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        if (joinedAt == null) joinedAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }

    public ProjectMember() {}

    public ProjectMember(Long id, Long projectId, Long userId, String roleApplied, String message, String status, LocalDateTime joinedAt) {
        this.id = id;
        this.projectId = projectId;
        this.userId = userId;
        this.roleApplied = roleApplied;
        this.message = message;
        this.status = status;
        this.joinedAt = joinedAt;
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

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRoleApplied() {
        return this.roleApplied;
    }

    public void setRoleApplied(String roleApplied) {
        this.roleApplied = roleApplied;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getJoinedAt() {
        return this.joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public static ProjectMemberBuilder builder() {
        return new ProjectMemberBuilder();
    }

    public static class ProjectMemberBuilder {
        private Long id;
        private Long projectId;
        private Long userId;
        private String roleApplied;
        private String message;
        private String status = "PENDING";
        private LocalDateTime joinedAt;

        public ProjectMemberBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ProjectMemberBuilder projectId(Long projectId) {
            this.projectId = projectId;
            return this;
        }

        public ProjectMemberBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public ProjectMemberBuilder roleApplied(String roleApplied) {
            this.roleApplied = roleApplied;
            return this;
        }

        public ProjectMemberBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ProjectMemberBuilder status(String status) {
            this.status = status;
            return this;
        }

        public ProjectMemberBuilder joinedAt(LocalDateTime joinedAt) {
            this.joinedAt = joinedAt;
            return this;
        }

        public ProjectMember build() {
            return new ProjectMember(this.id, this.projectId, this.userId, this.roleApplied, this.message, this.status, this.joinedAt);
        }
    }

}
