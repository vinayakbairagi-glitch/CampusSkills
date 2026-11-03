package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_resources")
public class GroupResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long groupId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String resourceType; // LINK, YOUTUBE, PDF, DOCUMENT

    @Column(nullable = false, length = 1000)
    private String urlOrPath;

    @Column(nullable = false)
    private Long uploadedBy;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public GroupResource() {}

    public GroupResource(Long id, Long groupId, String title, String resourceType, String urlOrPath, Long uploadedBy, LocalDateTime createdAt) {
        this.id = id;
        this.groupId = groupId;
        this.title = title;
        this.resourceType = resourceType;
        this.urlOrPath = urlOrPath;
        this.uploadedBy = uploadedBy;
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

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResourceType() {
        return this.resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getUrlOrPath() {
        return this.urlOrPath;
    }

    public void setUrlOrPath(String urlOrPath) {
        this.urlOrPath = urlOrPath;
    }

    public Long getUploadedBy() {
        return this.uploadedBy;
    }

    public void setUploadedBy(Long uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static GroupResourceBuilder builder() {
        return new GroupResourceBuilder();
    }

    public static class GroupResourceBuilder {
        private Long id;
        private Long groupId;
        private String title;
        private String resourceType;
        private String urlOrPath;
        private Long uploadedBy;
        private LocalDateTime createdAt;

        public GroupResourceBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public GroupResourceBuilder groupId(Long groupId) {
            this.groupId = groupId;
            return this;
        }

        public GroupResourceBuilder title(String title) {
            this.title = title;
            return this;
        }

        public GroupResourceBuilder resourceType(String resourceType) {
            this.resourceType = resourceType;
            return this;
        }

        public GroupResourceBuilder urlOrPath(String urlOrPath) {
            this.urlOrPath = urlOrPath;
            return this;
        }

        public GroupResourceBuilder uploadedBy(Long uploadedBy) {
            this.uploadedBy = uploadedBy;
            return this;
        }

        public GroupResourceBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public GroupResource build() {
            return new GroupResource(this.id, this.groupId, this.title, this.resourceType, this.urlOrPath, this.uploadedBy, this.createdAt);
        }
    }

}
