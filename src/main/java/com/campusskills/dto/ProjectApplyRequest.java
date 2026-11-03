package com.campusskills.dto;

import jakarta.validation.constraints.NotBlank;
public class ProjectApplyRequest {
    @NotBlank(message = "Role is required")
    private String roleApplied;

    private String message;

    public ProjectApplyRequest() {}

    public ProjectApplyRequest(String roleApplied, String message) {
        this.roleApplied = roleApplied;
        this.message = message;
    }

    public String getRoleApplied() {
        return this.roleApplied;
    }

    public void setRoleApplied(String roleApplied) {
        this.roleApplied = roleApplied;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
