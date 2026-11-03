package com.campusskills.dto;

import jakarta.validation.constraints.NotBlank;
public class QuestionBankRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Topic is required")
    private String topic;

    public QuestionBankRequest() {}

    public QuestionBankRequest(String title, String subject, String topic) {
        this.title = title;
        this.subject = subject;
        this.topic = topic;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
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

}
