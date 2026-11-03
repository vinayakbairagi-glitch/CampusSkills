package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "xp_events")
public class XpEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer xpAmount;

    @Column(nullable = false)
    private String reason;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public XpEvent() {}

    public XpEvent(Long id, Long userId, Integer xpAmount, String reason, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.xpAmount = xpAmount;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getXpAmount() {
        return this.xpAmount;
    }

    public void setXpAmount(Integer xpAmount) {
        this.xpAmount = xpAmount;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static XpEventBuilder builder() {
        return new XpEventBuilder();
    }

    public static class XpEventBuilder {
        private Long id;
        private Long userId;
        private Integer xpAmount;
        private String reason;
        private LocalDateTime createdAt;

        public XpEventBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public XpEventBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public XpEventBuilder xpAmount(Integer xpAmount) {
            this.xpAmount = xpAmount;
            return this;
        }

        public XpEventBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public XpEventBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public XpEvent build() {
            return new XpEvent(this.id, this.userId, this.xpAmount, this.reason, this.createdAt);
        }
    }

}
