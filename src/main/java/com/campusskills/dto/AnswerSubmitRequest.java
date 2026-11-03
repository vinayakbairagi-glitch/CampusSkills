package com.campusskills.dto;

import jakarta.validation.constraints.NotNull;
public class AnswerSubmitRequest {
    @NotNull(message = "Question ID is required")
    private Long questionId;

    private String answer;

    public AnswerSubmitRequest() {}

    public AnswerSubmitRequest(Long questionId, String answer) {
        this.questionId = questionId;
        this.answer = answer;
    }

    public Long getQuestionId() {
        return this.questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

}
