package com.campusskills.entity;

import jakarta.persistence.*;
@Entity
@Table(name = "student_answers", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"attemptId", "questionId"})
})
public class StudentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long attemptId;

    @Column(nullable = false)
    private Long questionId;

    @Column(length = 2000)
    private String studentAnswer;

    private Boolean isCorrect = false;

    private Double marksAwarded = 0.0;

    public StudentAnswer() {}

    public StudentAnswer(Long id, Long attemptId, Long questionId, String studentAnswer, Boolean isCorrect, Double marksAwarded) {
        this.id = id;
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.studentAnswer = studentAnswer;
        this.isCorrect = isCorrect;
        this.marksAwarded = marksAwarded;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAttemptId() {
        return this.attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getQuestionId() {
        return this.questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getStudentAnswer() {
        return this.studentAnswer;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    public Boolean getIsCorrect() {
        return this.isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Double getMarksAwarded() {
        return this.marksAwarded;
    }

    public void setMarksAwarded(Double marksAwarded) {
        this.marksAwarded = marksAwarded;
    }

    public static StudentAnswerBuilder builder() {
        return new StudentAnswerBuilder();
    }

    public static class StudentAnswerBuilder {
        private Long id;
        private Long attemptId;
        private Long questionId;
        private String studentAnswer;
        private Boolean isCorrect = false;
        private Double marksAwarded = 0.0;

        public StudentAnswerBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public StudentAnswerBuilder attemptId(Long attemptId) {
            this.attemptId = attemptId;
            return this;
        }

        public StudentAnswerBuilder questionId(Long questionId) {
            this.questionId = questionId;
            return this;
        }

        public StudentAnswerBuilder studentAnswer(String studentAnswer) {
            this.studentAnswer = studentAnswer;
            return this;
        }

        public StudentAnswerBuilder isCorrect(Boolean isCorrect) {
            this.isCorrect = isCorrect;
            return this;
        }

        public StudentAnswerBuilder marksAwarded(Double marksAwarded) {
            this.marksAwarded = marksAwarded;
            return this;
        }

        public StudentAnswer build() {
            return new StudentAnswer(this.id, this.attemptId, this.questionId, this.studentAnswer, this.isCorrect, this.marksAwarded);
        }
    }

}
