package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "skills")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String category;
    private String description;
    private Long createdBy;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Skill() {}

    public Skill(Long id, String name, String category, String description, Long createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.createdBy = createdBy;
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

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public static SkillBuilder builder() {
        return new SkillBuilder();
    }

    public static class SkillBuilder {
        private Long id;
        private String name;
        private String category;
        private String description;
        private Long createdBy;
        private LocalDateTime createdAt;

        public SkillBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SkillBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SkillBuilder category(String category) {
            this.category = category;
            return this;
        }

        public SkillBuilder description(String description) {
            this.description = description;
            return this;
        }

        public SkillBuilder createdBy(Long createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public SkillBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Skill build() {
            return new Skill(this.id, this.name, this.category, this.description, this.createdBy, this.createdAt);
        }
    }

}
