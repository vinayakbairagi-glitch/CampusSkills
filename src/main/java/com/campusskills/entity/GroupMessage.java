package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_messages")
public class GroupMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long groupId;

    @Column(nullable = false)
    private Long senderId;

    private String senderName;
    private String senderPhoto;

    @Column(nullable = false, length = 4000)
    private String content;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public GroupMessage() {}

    public GroupMessage(Long id, Long groupId, Long senderId, String senderName, String senderPhoto, String content, LocalDateTime createdAt) {
        this.id = id;
        this.groupId = groupId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderPhoto = senderPhoto;
        this.content = content;
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

    public Long getSenderId() {
        return this.senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhoto() {
        return this.senderPhoto;
    }

    public void setSenderPhoto(String senderPhoto) {
        this.senderPhoto = senderPhoto;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static GroupMessageBuilder builder() {
        return new GroupMessageBuilder();
    }

    public static class GroupMessageBuilder {
        private Long id;
        private Long groupId;
        private Long senderId;
        private String senderName;
        private String senderPhoto;
        private String content;
        private LocalDateTime createdAt;

        public GroupMessageBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public GroupMessageBuilder groupId(Long groupId) {
            this.groupId = groupId;
            return this;
        }

        public GroupMessageBuilder senderId(Long senderId) {
            this.senderId = senderId;
            return this;
        }

        public GroupMessageBuilder senderName(String senderName) {
            this.senderName = senderName;
            return this;
        }

        public GroupMessageBuilder senderPhoto(String senderPhoto) {
            this.senderPhoto = senderPhoto;
            return this;
        }

        public GroupMessageBuilder content(String content) {
            this.content = content;
            return this;
        }

        public GroupMessageBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public GroupMessage build() {
            return new GroupMessage(this.id, this.groupId, this.senderId, this.senderName, this.senderPhoto, this.content, this.createdAt);
        }
    }

}
