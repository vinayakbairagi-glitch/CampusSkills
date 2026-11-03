package com.campusskills.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public class ReportCreateRequest {
    @NotBlank(message = "Reported type is required (DOUBT, ANSWER, GROUP, USER)")
    private String reportedType;

    @NotNull(message = "Reported ID is required")
    private Long reportedId;

    @NotBlank(message = "Reason is required")
    private String reason;

    public ReportCreateRequest() {}

    public ReportCreateRequest(String reportedType, Long reportedId, String reason) {
        this.reportedType = reportedType;
        this.reportedId = reportedId;
        this.reason = reason;
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

}
