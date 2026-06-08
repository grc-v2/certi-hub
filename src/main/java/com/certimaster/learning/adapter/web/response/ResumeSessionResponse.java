package com.certimaster.learning.adapter.web.response;

import java.time.LocalDateTime;
import java.util.List;

public class ResumeSessionResponse {

    private Long id;
    private Long examId;
    private String examTitle;
    private Long certificationId;
    private String certificationName;
    private String mode;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime deadline;
    private Integer durationMinutes;
    private Integer totalQuestions;
    private Integer answeredCount;
    private Integer unansweredCount;
    private Integer flaggedCount;
    private Integer timeSpentSeconds;
    private List<QuestionWithAnswerResponse> questions;

    public ResumeSessionResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }

    public Long getCertificationId() {
        return certificationId;
    }

    public void setCertificationId(Long certificationId) {
        this.certificationId = certificationId;
    }

    public String getCertificationName() {
        return certificationName;
    }

    public void setCertificationName(String certificationName) {
        this.certificationName = certificationName;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getAnsweredCount() {
        return answeredCount;
    }

    public void setAnsweredCount(Integer answeredCount) {
        this.answeredCount = answeredCount;
    }

    public Integer getUnansweredCount() {
        return unansweredCount;
    }

    public void setUnansweredCount(Integer unansweredCount) {
        this.unansweredCount = unansweredCount;
    }

    public Integer getFlaggedCount() {
        return flaggedCount;
    }

    public void setFlaggedCount(Integer flaggedCount) {
        this.flaggedCount = flaggedCount;
    }

    public Integer getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(Integer timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public List<QuestionWithAnswerResponse> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionWithAnswerResponse> questions) {
        this.questions = questions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long examId;
        private String examTitle;
        private Long certificationId;
        private String certificationName;
        private String mode;
        private String status;
        private LocalDateTime startTime;
        private LocalDateTime deadline;
        private Integer durationMinutes;
        private Integer totalQuestions;
        private Integer answeredCount;
        private Integer unansweredCount;
        private Integer flaggedCount;
        private Integer timeSpentSeconds;
        private List<QuestionWithAnswerResponse> questions;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder examId(Long v) { this.examId = v; return this; }
        public Builder examTitle(String v) { this.examTitle = v; return this; }
        public Builder certificationId(Long v) { this.certificationId = v; return this; }
        public Builder certificationName(String v) { this.certificationName = v; return this; }
        public Builder mode(String v) { this.mode = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder startTime(LocalDateTime v) { this.startTime = v; return this; }
        public Builder deadline(LocalDateTime v) { this.deadline = v; return this; }
        public Builder durationMinutes(Integer v) { this.durationMinutes = v; return this; }
        public Builder totalQuestions(Integer v) { this.totalQuestions = v; return this; }
        public Builder answeredCount(Integer v) { this.answeredCount = v; return this; }
        public Builder unansweredCount(Integer v) { this.unansweredCount = v; return this; }
        public Builder flaggedCount(Integer v) { this.flaggedCount = v; return this; }
        public Builder timeSpentSeconds(Integer v) { this.timeSpentSeconds = v; return this; }
        public Builder questions(List<QuestionWithAnswerResponse> v) { this.questions = v; return this; }

        public ResumeSessionResponse build() {
            ResumeSessionResponse o = new ResumeSessionResponse();
            o.id = this.id;
            o.examId = this.examId;
            o.examTitle = this.examTitle;
            o.certificationId = this.certificationId;
            o.certificationName = this.certificationName;
            o.mode = this.mode;
            o.status = this.status;
            o.startTime = this.startTime;
            o.deadline = this.deadline;
            o.durationMinutes = this.durationMinutes;
            o.totalQuestions = this.totalQuestions;
            o.answeredCount = this.answeredCount;
            o.unansweredCount = this.unansweredCount;
            o.flaggedCount = this.flaggedCount;
            o.timeSpentSeconds = this.timeSpentSeconds;
            o.questions = this.questions;
            return o;
        }
    }
}
