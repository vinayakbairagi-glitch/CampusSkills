package com.campusskills.dto;

import jakarta.validation.constraints.NotNull;
public class SwapRequestCreate {
    @NotNull(message = "Receiver ID is required")
    private Long receiverId;

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    private String message;

    public SwapRequestCreate() {}

    public SwapRequestCreate(Long receiverId, Long skillId, String message) {
        this.receiverId = receiverId;
        this.skillId = skillId;
        this.message = message;
    }

    public Long getReceiverId() {
        return this.receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getSkillId() {
        return this.skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
