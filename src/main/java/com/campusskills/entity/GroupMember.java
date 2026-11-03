package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_members")
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long groupId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String role = "MEMBER"; // ADMIN, MEMBER

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE, PENDING

    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        if (joinedAt == null) joinedAt = LocalDateTime.now();
        if (role == null) role = "MEMBER";
        if (status == null) status = "ACTIVE";
    }

    public GroupMember() {}

    public GroupMember(Long id, Long groupId, Long userId, String role, String status, LocalDateTime joinedAt) {
        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
        this.role = role;
        this.status = status;
        this.joinedAt = joinedAt;
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

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public static GroupMemberBuilder builder() {
        return new GroupMemberBuilder();
    }

    public static class GroupMemberBuilder {
        private Long id;
        private Long groupId;
        private Long userId;
        private String role = "MEMBER";
        private String status = "ACTIVE";
        private LocalDateTime joinedAt;

        public GroupMemberBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public GroupMemberBuilder groupId(Long groupId) {
            this.groupId = groupId;
            return this;
        }

        public GroupMemberBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public GroupMemberBuilder role(String role) {
            this.role = role;
            return this;
        }

        public GroupMemberBuilder status(String status) {
            this.status = status;
            return this;
        }

        public GroupMemberBuilder joinedAt(LocalDateTime joinedAt) {
            this.joinedAt = joinedAt;
            return this;
        }

        public GroupMember build() {
            return new GroupMember(this.id, this.groupId, this.userId, this.role, this.status, this.joinedAt);
        }
    }

}
