package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sessionId;
    private Long projectId;

    @Column(nullable = false)
    private Long raterId;

    @Column(nullable = false)
    private Long ratedUserId;

    @Column(nullable = false)
    private Integer rating; // 1 to 5

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false)
    private String context; // SESSION, PROJECT

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Rating() {}

    public Rating(Long id, Long sessionId, Long projectId, Long raterId, Long ratedUserId, Integer rating, String comment, String context, LocalDateTime createdAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.projectId = projectId;
        this.raterId = raterId;
        this.ratedUserId = ratedUserId;
        this.rating = rating;
        this.comment = comment;
        this.context = context;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getRaterId() {
        return this.raterId;
    }

    public void setRaterId(Long raterId) {
        this.raterId = raterId;
    }

    public Long getRatedUserId() {
        return this.ratedUserId;
    }

    public void setRatedUserId(Long ratedUserId) {
        this.ratedUserId = ratedUserId;
    }

    public Integer getRating() {
        return this.rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getContext() {
        return this.context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static RatingBuilder builder() {
        return new RatingBuilder();
    }

    public static class RatingBuilder {
        private Long id;
        private Long sessionId;
        private Long projectId;
        private Long raterId;
        private Long ratedUserId;
        private Integer rating;
        private String comment;
        private String context;
        private LocalDateTime createdAt;

        public RatingBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RatingBuilder sessionId(Long sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public RatingBuilder projectId(Long projectId) {
            this.projectId = projectId;
            return this;
        }

        public RatingBuilder raterId(Long raterId) {
            this.raterId = raterId;
            return this;
        }

        public RatingBuilder ratedUserId(Long ratedUserId) {
            this.ratedUserId = ratedUserId;
            return this;
        }

        public RatingBuilder rating(Integer rating) {
            this.rating = rating;
            return this;
        }

        public RatingBuilder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public RatingBuilder context(String context) {
            this.context = context;
            return this;
        }

        public RatingBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Rating build() {
            return new Rating(this.id, this.sessionId, this.projectId, this.raterId, this.ratedUserId, this.rating, this.comment, this.context, this.createdAt);
        }
    }

}
