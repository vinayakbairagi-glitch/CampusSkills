package com.campusskills.dto;

import jakarta.validation.constraints.NotBlank;
public class DoubtCreateRequest {
    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Topic is required")
    private String topic;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String attachmentUrl;

    public DoubtCreateRequest() {}

    public DoubtCreateRequest(String subject, String topic, String title, String description, String attachmentUrl) {
        this.subject = subject;
        this.topic = topic;
        this.title = title;
        this.description = description;
        this.attachmentUrl = attachmentUrl;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttachmentUrl() {
        return this.attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

}
