package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "doubts")
public class Doubt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long askerId;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    private String attachmentUrl;

    @Column(nullable = false)
    private String status = "OPEN"; // OPEN, SOLVED

    private Long bestAnswerId;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = "OPEN";
    }

    public Doubt() {}

    public Doubt(Long id, Long askerId, String subject, String topic, String title, String description, String attachmentUrl, String status, Long bestAnswerId, LocalDateTime createdAt) {
        this.id = id;
        this.askerId = askerId;
        this.subject = subject;
        this.topic = topic;
        this.title = title;
        this.description = description;
        this.attachmentUrl = attachmentUrl;
        this.status = status;
        this.bestAnswerId = bestAnswerId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAskerId() {
        return this.askerId;
    }

    public void setAskerId(Long askerId) {
        this.askerId = askerId;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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

    public String getAttachmentUrl() {
        return this.attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getBestAnswerId() {
        return this.bestAnswerId;
    }

    public void setBestAnswerId(Long bestAnswerId) {
        this.bestAnswerId = bestAnswerId;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static DoubtBuilder builder() {
        return new DoubtBuilder();
    }

    public static class DoubtBuilder {
        private Long id;
        private Long askerId;
        private String subject;
        private String topic;
        private String title;
        private String description;
        private String attachmentUrl;
        private String status = "OPEN";
        private Long bestAnswerId;
        private LocalDateTime createdAt;

        public DoubtBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DoubtBuilder askerId(Long askerId) {
            this.askerId = askerId;
            return this;
        }

        public DoubtBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public DoubtBuilder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public DoubtBuilder title(String title) {
            this.title = title;
            return this;
        }

        public DoubtBuilder description(String description) {
            this.description = description;
            return this;
        }

        public DoubtBuilder attachmentUrl(String attachmentUrl) {
            this.attachmentUrl = attachmentUrl;
            return this;
        }

        public DoubtBuilder status(String status) {
            this.status = status;
            return this;
        }

        public DoubtBuilder bestAnswerId(Long bestAnswerId) {
            this.bestAnswerId = bestAnswerId;
            return this;
        }

        public DoubtBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Doubt build() {
            return new Doubt(this.id, this.askerId, this.subject, this.topic, this.title, this.description, this.attachmentUrl, this.status, this.bestAnswerId, this.createdAt);
        }
    }

}
