package com.campusskills.entity;

import jakarta.persistence.*;
@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bankId;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String questionType; // MCQ, TRUE_FALSE, FILL_BLANK, NUMERIC, MATCH, SHORT

    @Column(nullable = false)
    private String difficulty = "MEDIUM"; // EASY, MEDIUM, HARD

    @Column(nullable = false, length = 3000)
    private String questionText;

    @Column(length = 2000)
    private String optionsJson; // JSON array of options for MCQ/TRUE_FALSE e.g. ["A","B","C","D"]

    @Column(nullable = false, length = 1000)
    private String correctAnswer; // For FILL_BLANK alternatives separated by '|', NUMERIC numeric string, etc.

    @Column(length = 2000)
    private String explanation;

    private Double marks = 1.0;

    private Double negativeMarks = 0.0;

    @Column(length = 2000)
    private String matchPairsJson; // JSON array of left-right pairs for MATCH type

    public Question() {}

    public Question(Long id, Long bankId, String subject, String topic, String questionType, String difficulty, String questionText, String optionsJson, String correctAnswer, String explanation, Double marks, Double negativeMarks, String matchPairsJson) {
        this.id = id;
        this.bankId = bankId;
        this.subject = subject;
        this.topic = topic;
        this.questionType = questionType;
        this.difficulty = difficulty;
        this.questionText = questionText;
        this.optionsJson = optionsJson;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.marks = marks;
        this.negativeMarks = negativeMarks;
        this.matchPairsJson = matchPairsJson;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getOptionsJson() {
        return this.optionsJson;
    }

    public void setOptionsJson(String optionsJson) {
        this.optionsJson = optionsJson;
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

    public static QuestionBuilder builder() {
        return new QuestionBuilder();
    }

    public static class QuestionBuilder {
        private Long id;
        private Long bankId;
        private String subject;
        private String topic;
        private String questionType;
        private String difficulty = "MEDIUM";
        private String questionText;
        private String optionsJson;
        private String correctAnswer;
        private String explanation;
        private Double marks = 1.0;
        private Double negativeMarks = 0.0;
        private String matchPairsJson;

        public QuestionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public QuestionBuilder bankId(Long bankId) {
            this.bankId = bankId;
            return this;
        }

        public QuestionBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public QuestionBuilder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public QuestionBuilder questionType(String questionType) {
            this.questionType = questionType;
            return this;
        }

        public QuestionBuilder difficulty(String difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        public QuestionBuilder questionText(String questionText) {
            this.questionText = questionText;
            return this;
        }

        public QuestionBuilder optionsJson(String optionsJson) {
            this.optionsJson = optionsJson;
            return this;
        }

        public QuestionBuilder correctAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer;
            return this;
        }

        public QuestionBuilder explanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public QuestionBuilder marks(Double marks) {
            this.marks = marks;
            return this;
        }

        public QuestionBuilder negativeMarks(Double negativeMarks) {
            this.negativeMarks = negativeMarks;
            return this;
        }

        public QuestionBuilder matchPairsJson(String matchPairsJson) {
            this.matchPairsJson = matchPairsJson;
            return this;
        }

        public Question build() {
            return new Question(this.id, this.bankId, this.subject, this.topic, this.questionType, this.difficulty, this.questionText, this.optionsJson, this.correctAnswer, this.explanation, this.marks, this.negativeMarks, this.matchPairsJson);
        }
    }

}
