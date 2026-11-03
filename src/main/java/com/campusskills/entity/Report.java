package com.campusskills.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reporterId;

    @Column(nullable = false)
    private String reportedType; // DOUBT, ANSWER, GROUP, USER

    @Column(nullable = false)
    private Long reportedId;

    @Lob
    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, DISMISSED, ACTIONED

    private String actionTaken; // e.g. REMOVED, SUSPENDED, DISMISSED

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }

    public Report() {}

    public Report(Long id, Long reporterId, String reportedType, Long reportedId, String reason, String status, String actionTaken, LocalDateTime createdAt) {
        this.id = id;
        this.reporterId = reporterId;
        this.reportedType = reportedType;
        this.reportedId = reportedId;
        this.reason = reason;
        this.status = status;
        this.actionTaken = actionTaken;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReporterId() {
        return this.reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public String getReportedType() {
        return this.reportedType;
    }

    public void setReportedType(String reportedType) {
        this.reportedType = reportedType;
    }

    public Long getReportedId() {
        return this.reportedId;
    }

    public void setReportedId(Long reportedId) {
        this.reportedId = reportedId;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActionTaken() {
        return this.actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static ReportBuilder builder() {
        return new ReportBuilder();
    }

    public static class ReportBuilder {
        private Long id;
        private Long reporterId;
        private String reportedType;
        private Long reportedId;
        private String reason;
        private String status = "PENDING";
        private String actionTaken;
        private LocalDateTime createdAt;

        public ReportBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ReportBuilder reporterId(Long reporterId) {
            this.reporterId = reporterId;
            return this;
        }

        public ReportBuilder reportedType(String reportedType) {
            this.reportedType = reportedType;
            return this;
        }

        public ReportBuilder reportedId(Long reportedId) {
            this.reportedId = reportedId;
            return this;
        }

        public ReportBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public ReportBuilder status(String status) {
            this.status = status;
            return this;
        }

        public ReportBuilder actionTaken(String actionTaken) {
            this.actionTaken = actionTaken;
            return this;
        }

        public ReportBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Report build() {
            return new Report(this.id, this.reporterId, this.reportedType, this.reportedId, this.reason, this.status, this.actionTaken, this.createdAt);
        }
    }

}
