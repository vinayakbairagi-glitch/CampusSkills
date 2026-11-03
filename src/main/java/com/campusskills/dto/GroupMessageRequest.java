package com.campusskills.dto;

import jakarta.validation.constraints.NotBlank;
public class GroupMessageRequest {
    @NotBlank(message = "Message content is required")
    private String content;

    public GroupMessageRequest() {}

    public GroupMessageRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
