package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "doubt_answers")
public class DoubtAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long doubtId;

    @Column(nullable = false)
    private Long authorId;

    private String authorName;
    private Integer authorYear;

    @Column(nullable = false, length = 4000)
    private String content;

    private String attachmentUrl;

    private Boolean isBest = false;

    private Boolean isVerified = false;

    private Long verifiedBy;

    private Integer upvoteCount = 0;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (isBest == null) isBest = false;
        if (isVerified == null) isVerified = false;
        if (upvoteCount == null) upvoteCount = 0;
    }

    public DoubtAnswer() {}

    public DoubtAnswer(Long id, Long doubtId, Long authorId, String authorName, Integer authorYear, String content, String attachmentUrl, Boolean isBest, Boolean isVerified, Long verifiedBy, Integer upvoteCount, LocalDateTime createdAt) {
        this.id = id;
        this.doubtId = doubtId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorYear = authorYear;
        this.content = content;
        this.attachmentUrl = attachmentUrl;
        this.isBest = isBest;
        this.isVerified = isVerified;
        this.verifiedBy = verifiedBy;
        this.upvoteCount = upvoteCount;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDoubtId() {
        return this.doubtId;
    }

    public void setDoubtId(Long doubtId) {
        this.doubtId = doubtId;
    }

    public Long getAuthorId() {
        return this.authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return this.authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Integer getAuthorYear() {
        return this.authorYear;
    }

    public void setAuthorYear(Integer authorYear) {
        this.authorYear = authorYear;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttachmentUrl() {
        return this.attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public Boolean getIsBest() {
        return this.isBest;
    }

    public void setIsBest(Boolean isBest) {
        this.isBest = isBest;
    }

    public Boolean getIsVerified() {
        return this.isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public Long getVerifiedBy() {
        return this.verifiedBy;
    }

    public void setVerifiedBy(Long verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public Integer getUpvoteCount() {
        return this.upvoteCount;
    }

    public void setUpvoteCount(Integer upvoteCount) {
        this.upvoteCount = upvoteCount;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static DoubtAnswerBuilder builder() {
        return new DoubtAnswerBuilder();
    }

    public static class DoubtAnswerBuilder {
        private Long id;
        private Long doubtId;
        private Long authorId;
        private String authorName;
        private Integer authorYear;
        private String content;
        private String attachmentUrl;
        private Boolean isBest = false;
        private Boolean isVerified = false;
        private Long verifiedBy;
        private Integer upvoteCount = 0;
        private LocalDateTime createdAt;

        public DoubtAnswerBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DoubtAnswerBuilder doubtId(Long doubtId) {
            this.doubtId = doubtId;
            return this;
        }

        public DoubtAnswerBuilder authorId(Long authorId) {
            this.authorId = authorId;
            return this;
        }

        public DoubtAnswerBuilder authorName(String authorName) {
            this.authorName = authorName;
            return this;
        }

        public DoubtAnswerBuilder authorYear(Integer authorYear) {
            this.authorYear = authorYear;
            return this;
        }

        public DoubtAnswerBuilder content(String content) {
            this.content = content;
            return this;
        }

        public DoubtAnswerBuilder attachmentUrl(String attachmentUrl) {
            this.attachmentUrl = attachmentUrl;
            return this;
        }

        public DoubtAnswerBuilder isBest(Boolean isBest) {
            this.isBest = isBest;
            return this;
        }

        public DoubtAnswerBuilder isVerified(Boolean isVerified) {
            this.isVerified = isVerified;
            return this;
        }

        public DoubtAnswerBuilder verifiedBy(Long verifiedBy) {
            this.verifiedBy = verifiedBy;
            return this;
        }

        public DoubtAnswerBuilder upvoteCount(Integer upvoteCount) {
            this.upvoteCount = upvoteCount;
            return this;
        }

        public DoubtAnswerBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DoubtAnswer build() {
            return new DoubtAnswer(this.id, this.doubtId, this.authorId, this.authorName, this.authorYear, this.content, this.attachmentUrl, this.isBest, this.isVerified, this.verifiedBy, this.upvoteCount, this.createdAt);
        }
    }

}
