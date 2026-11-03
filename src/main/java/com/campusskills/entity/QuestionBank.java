package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "question_banks")
public class QuestionBank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String topic;

    private Long createdBy;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public QuestionBank() {}

    public QuestionBank(Long id, String title, String subject, String topic, Long createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.topic = topic;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public static QuestionBankBuilder builder() {
        return new QuestionBankBuilder();
    }

    public static class QuestionBankBuilder {
        private Long id;
        private String title;
        private String subject;
        private String topic;
        private Long createdBy;
        private LocalDateTime createdAt;

        public QuestionBankBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public QuestionBankBuilder title(String title) {
            this.title = title;
            return this;
        }

        public QuestionBankBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public QuestionBankBuilder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public QuestionBankBuilder createdBy(Long createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public QuestionBankBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public QuestionBank build() {
            return new QuestionBank(this.id, this.title, this.subject, this.topic, this.createdBy, this.createdAt);
        }
    }

}
