package com.campusskills.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StudyNoteRequest {
    @NotBlank(message = "Note title is required")
    @Size(max = 120, message = "Note title must be 120 characters or fewer")
    private String title;

    @Size(max = 20000, message = "Note content must be 20,000 characters or fewer")
    private String content;

    public StudyNoteRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
