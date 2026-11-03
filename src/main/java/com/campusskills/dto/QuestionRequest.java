package com.campusskills.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class QuestionRequest {
    private Long bankId;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Topic is required")
    private String topic;

    @NotBlank(message = "Question type is required (MCQ, TRUE_FALSE, FILL_BLANK, NUMERIC, MATCH, SHORT)")
    private String questionType;

    private String difficulty = "MEDIUM"; // EASY, MEDIUM, HARD

    @NotBlank(message = "Question text is required")
    private String questionText;

    private List<String> options; // for MCQ / TRUE_FALSE
    private String correctAnswer;
    private String explanation;
    private Double marks = 1.0;
    private Double negativeMarks = 0.0;
    private String matchPairsJson;

    public QuestionRequest() {}

    public QuestionRequest(Long bankId, String subject, String topic, String questionType, String difficulty, String questionText, List<String> options, String correctAnswer, String explanation, Double marks, Double negativeMarks, String matchPairsJson) {
        this.bankId = bankId;
        this.subject = subject;
        this.topic = topic;
        this.questionType = questionType;
        this.difficulty = difficulty;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.marks = marks;
        this.negativeMarks = negativeMarks;
        this.matchPairsJson = matchPairsJson;
    }

    public Long getBankId() {
        return this.bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
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

    public String getQuestionType() {
        return this.questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getDifficulty() {
        return this.difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestionText() {
        return this.questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<String> getOptions() {
        return this.options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getCorrectAnswer() {
        return this.correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getExplanation() {
        return this.explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Double getMarks() {
        return this.marks;
    }

    public void setMarks(Double marks) {
        this.marks = marks;
    }

    public Double getNegativeMarks() {
        return this.negativeMarks;
    }

    public void setNegativeMarks(Double negativeMarks) {
        this.negativeMarks = negativeMarks;
    }

    public String getMatchPairsJson() {
        return this.matchPairsJson;
    }

    public void setMatchPairsJson(String matchPairsJson) {
        this.matchPairsJson = matchPairsJson;
    }

}
