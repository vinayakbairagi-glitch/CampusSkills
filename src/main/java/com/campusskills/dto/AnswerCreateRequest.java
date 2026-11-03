package com.campusskills.dto;

import jakarta.validation.constraints.NotBlank;
public class AnswerCreateRequest {
    @NotBlank(message = "Answer content is required")
    private String content;

    private String attachmentUrl;

    public AnswerCreateRequest() {}

    public AnswerCreateRequest(String content, String attachmentUrl) {
        this.content = content;
        this.attachmentUrl = attachmentUrl;
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

}
