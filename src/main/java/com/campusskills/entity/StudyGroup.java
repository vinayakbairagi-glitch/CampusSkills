package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "study_groups")
public class StudyGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String category; // SUBJECT, EXAM, SKILL, PLACEMENT

    private String branch;
    private Integer studyYear;
    private String timing;

    @Column(nullable = false)
    private String mode = "ONLINE"; // ONLINE, OFFLINE, HYBRID

    private Integer memberLimit = 30;

    @Column(nullable = false)
    private String visibility = "PUBLIC"; // PUBLIC, PRIVATE

    @Column(nullable = false)
    private Long createdBy;

    @Column(length = 2000)
    private String description;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (mode == null) mode = "ONLINE";
        if (visibility == null) visibility = "PUBLIC";
        if (memberLimit == null) memberLimit = 30;
    }

    public StudyGroup() {}

    public StudyGroup(Long id, String name, String subject, String category, String branch, Integer studyYear, String timing, String mode, Integer memberLimit, String visibility, Long createdBy, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.category = category;
        this.branch = branch;
        this.studyYear = studyYear;
        this.timing = timing;
        this.mode = mode;
        this.memberLimit = memberLimit;
        this.visibility = visibility;
        this.createdBy = createdBy;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBranch() {
        return this.branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Integer getStudyYear() {
        return this.studyYear;
    }

    public void setStudyYear(Integer studyYear) {
        this.studyYear = studyYear;
    }

    public String getTiming() {
        return this.timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getMemberLimit() {
        return this.memberLimit;
    }

    public void setMemberLimit(Integer memberLimit) {
        this.memberLimit = memberLimit;
    }

    public String getVisibility() {
        return this.visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Long getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static StudyGroupBuilder builder() {
        return new StudyGroupBuilder();
    }

    public static class StudyGroupBuilder {
        private Long id;
        private String name;
        private String subject;
        private String category;
        private String branch;
        private Integer studyYear;
        private String timing;
        private String mode = "ONLINE";
        private Integer memberLimit = 30;
        private String visibility = "PUBLIC";
        private Long createdBy;
        private String description;
        private LocalDateTime createdAt;

        public StudyGroupBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public StudyGroupBuilder name(String name) {
            this.name = name;
            return this;
        }

        public StudyGroupBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public StudyGroupBuilder category(String category) {
            this.category = category;
            return this;
        }

        public StudyGroupBuilder branch(String branch) {
            this.branch = branch;
            return this;
        }

        public StudyGroupBuilder studyYear(Integer studyYear) {
            this.studyYear = studyYear;
            return this;
        }

        public StudyGroupBuilder timing(String timing) {
            this.timing = timing;
            return this;
        }

        public StudyGroupBuilder mode(String mode) {
            this.mode = mode;
            return this;
        }

        public StudyGroupBuilder memberLimit(Integer memberLimit) {
            this.memberLimit = memberLimit;
            return this;
        }

        public StudyGroupBuilder visibility(String visibility) {
            this.visibility = visibility;
            return this;
        }

        public StudyGroupBuilder createdBy(Long createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public StudyGroupBuilder description(String description) {
            this.description = description;
            return this;
        }

        public StudyGroupBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public StudyGroup build() {
            return new StudyGroup(this.id, this.name, this.subject, this.category, this.branch, this.studyYear, this.timing, this.mode, this.memberLimit, this.visibility, this.createdBy, this.description, this.createdAt);
        }
    }

}
