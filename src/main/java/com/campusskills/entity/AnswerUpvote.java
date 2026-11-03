package com.campusskills.entity;

import jakarta.persistence.*;
@Entity
@Table(name = "answer_upvotes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"answerId", "userId"})
})
public class AnswerUpvote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long answerId;

    @Column(nullable = false)
    private Long userId;

    public AnswerUpvote() {}

    public AnswerUpvote(Long id, Long answerId, Long userId) {
        this.id = id;
        this.answerId = answerId;
        this.userId = userId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnswerId() {
        return this.answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public static AnswerUpvoteBuilder builder() {
        return new AnswerUpvoteBuilder();
    }

    public static class AnswerUpvoteBuilder {
        private Long id;
        private Long answerId;
        private Long userId;

        public AnswerUpvoteBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AnswerUpvoteBuilder answerId(Long answerId) {
            this.answerId = answerId;
            return this;
        }

        public AnswerUpvoteBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public AnswerUpvote build() {
            return new AnswerUpvote(this.id, this.answerId, this.userId);
        }
    }

}
