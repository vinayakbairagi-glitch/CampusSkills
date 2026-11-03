package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "swap_requests")
public class SwapRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private Long receiverId;

    @Column(nullable = false)
    private Long skillId;

    private String skillName;

    @Column(length = 1000)
    private String message;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, ACCEPTED, REJECTED, CANCELLED

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public SwapRequest() {}

    public SwapRequest(Long id, Long senderId, Long receiverId, Long skillId, String skillName, String message, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return this.senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return this.receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
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

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static SwapRequestBuilder builder() {
        return new SwapRequestBuilder();
    }

    public static class SwapRequestBuilder {
        private Long id;
        private Long senderId;
        private Long receiverId;
        private Long skillId;
        private String skillName;
        private String message;
        private String status = "PENDING";
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public SwapRequestBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SwapRequestBuilder senderId(Long senderId) {
            this.senderId = senderId;
            return this;
        }

        public SwapRequestBuilder receiverId(Long receiverId) {
            this.receiverId = receiverId;
            return this;
        }

        public SwapRequestBuilder skillId(Long skillId) {
            this.skillId = skillId;
            return this;
        }

        public SwapRequestBuilder skillName(String skillName) {
            this.skillName = skillName;
            return this;
        }

        public SwapRequestBuilder message(String message) {
            this.message = message;
            return this;
        }

        public SwapRequestBuilder status(String status) {
            this.status = status;
            return this;
        }

        public SwapRequestBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public SwapRequestBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public SwapRequest build() {
            return new SwapRequest(this.id, this.senderId, this.receiverId, this.skillId, this.skillName, this.message, this.status, this.createdAt, this.updatedAt);
        }
    }

}
