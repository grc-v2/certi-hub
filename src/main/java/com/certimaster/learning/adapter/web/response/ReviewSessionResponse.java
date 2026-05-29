package com.certimaster.learning.adapter.web.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReviewSessionResponse {

    private Long id;
    private Long examId;
    private String examTitle;
    private Long certificationId;
    private String certificationName;
    private String mode;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalQuestions;
    private Integer correctCount;
    private Integer wrongCount;
    private BigDecimal scorePercentage;
    private Boolean passed;
    private Integer timeSpentSeconds;
    private List<ReviewQuestionResponse> questions;

    public ReviewSessionResponse() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public String getExamTitle() { return examTitle; }
    public void setExamTitle(String examTitle) { this.examTitle = examTitle; }

    public Long getCertificationId() { return certificationId; }
    public void setCertificationId(Long certificationId) { this.certificationId = certificationId; }

    public String getCertificationName() { return certificationName; }
    public void setCertificationName(String certificationName) { this.certificationName = certificationName; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public Integer getCorrectCount() { return correctCount; }
    public void setCorrectCount(Integer correctCount) { this.correctCount = correctCount; }

    public Integer getWrongCount() { return wrongCount; }
    public void setWrongCount(Integer wrongCount) { this.wrongCount = wrongCount; }

    public BigDecimal getScorePercentage() { return scorePercentage; }
    public void setScorePercentage(BigDecimal scorePercentage) { this.scorePercentage = scorePercentage; }

    public Boolean getPassed() { return passed; }
    public void setPassed(Boolean passed) { this.passed = passed; }

    public Integer getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(Integer timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }

    public List<ReviewQuestionResponse> getQuestions() { return questions; }
    public void setQuestions(List<ReviewQuestionResponse> questions) { this.questions = questions; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long examId;
        private String examTitle;
        private Long certificationId;
        private String certificationName;
        private String mode;
        private String status;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer totalQuestions;
        private Integer correctCount;
        private Integer wrongCount;
        private BigDecimal scorePercentage;
        private Boolean passed;
        private Integer timeSpentSeconds;
        private List<ReviewQuestionResponse> questions;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder examId(Long v) { this.examId = v; return this; }
        public Builder examTitle(String v) { this.examTitle = v; return this; }
        public Builder certificationId(Long v) { this.certificationId = v; return this; }
        public Builder certificationName(String v) { this.certificationName = v; return this; }
        public Builder mode(String v) { this.mode = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder startTime(LocalDateTime v) { this.startTime = v; return this; }
        public Builder endTime(LocalDateTime v) { this.endTime = v; return this; }
        public Builder totalQuestions(Integer v) { this.totalQuestions = v; return this; }
        public Builder correctCount(Integer v) { this.correctCount = v; return this; }
        public Builder wrongCount(Integer v) { this.wrongCount = v; return this; }
        public Builder scorePercentage(BigDecimal v) { this.scorePercentage = v; return this; }
        public Builder passed(Boolean v) { this.passed = v; return this; }
        public Builder timeSpentSeconds(Integer v) { this.timeSpentSeconds = v; return this; }
        public Builder questions(List<ReviewQuestionResponse> v) { this.questions = v; return this; }

        public ReviewSessionResponse build() {
            ReviewSessionResponse o = new ReviewSessionResponse();
            o.id = this.id;
            o.examId = this.examId;
            o.examTitle = this.examTitle;
            o.certificationId = this.certificationId;
            o.certificationName = this.certificationName;
            o.mode = this.mode;
            o.status = this.status;
            o.startTime = this.startTime;
            o.endTime = this.endTime;
            o.totalQuestions = this.totalQuestions;
            o.correctCount = this.correctCount;
            o.wrongCount = this.wrongCount;
            o.scorePercentage = this.scorePercentage;
            o.passed = this.passed;
            o.timeSpentSeconds = this.timeSpentSeconds;
            o.questions = this.questions;
            return o;
        }
    }
}
