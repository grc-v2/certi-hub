package com.certimaster.learning.adapter.web.response;

import com.certimaster.shared.dto.BaseResponse;
import com.certimaster.learning.adapter.web.response.QuestionResponse;

import java.time.LocalDateTime;
import java.util.List;

public class ExamSessionResponse extends BaseResponse {

    private Long examId;
    private String examTitle;
    private Long certificationId;
    private String certificationName;
    private String mode;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private Integer passingScore;
    private Integer totalQuestions;
    private Integer currentQuestionIndex;
    private List<QuestionResponse> questions;

    public ExamSessionResponse() {
    }

    public ExamSessionResponse(Long id, LocalDateTime createdAt, LocalDateTime updatedAt,
                                String createdBy, String updatedBy,
                                Long examId, String examTitle, Long certificationId,
                                String certificationName, String mode, String status,
                                LocalDateTime startTime, LocalDateTime endTime,
                                Integer durationMinutes, Integer passingScore,
                                Integer totalQuestions, Integer currentQuestionIndex,
                                List<QuestionResponse> questions) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.examId = examId;
        this.examTitle = examTitle;
        this.certificationId = certificationId;
        this.certificationName = certificationName;
        this.mode = mode;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.passingScore = passingScore;
        this.totalQuestions = totalQuestions;
        this.currentQuestionIndex = currentQuestionIndex;
        this.questions = questions;
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

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(Integer passingScore) {
        this.passingScore = passingScore;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void setCurrentQuestionIndex(Integer currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public List<QuestionResponse> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionResponse> questions) {
        this.questions = questions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Long examId;
        private String examTitle;
        private Long certificationId;
        private String certificationName;
        private String mode;
        private String status;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer durationMinutes;
        private Integer passingScore;
        private Integer totalQuestions;
        private Integer currentQuestionIndex;
        private List<QuestionResponse> questions;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public Builder createdBy(String v) { this.createdBy = v; return this; }
        public Builder updatedBy(String v) { this.updatedBy = v; return this; }
        public Builder examId(Long v) { this.examId = v; return this; }
        public Builder examTitle(String v) { this.examTitle = v; return this; }
        public Builder certificationId(Long v) { this.certificationId = v; return this; }
        public Builder certificationName(String v) { this.certificationName = v; return this; }
        public Builder mode(String v) { this.mode = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder startTime(LocalDateTime v) { this.startTime = v; return this; }
        public Builder endTime(LocalDateTime v) { this.endTime = v; return this; }
        public Builder durationMinutes(Integer v) { this.durationMinutes = v; return this; }
        public Builder passingScore(Integer v) { this.passingScore = v; return this; }
        public Builder totalQuestions(Integer v) { this.totalQuestions = v; return this; }
        public Builder currentQuestionIndex(Integer v) { this.currentQuestionIndex = v; return this; }
        public Builder questions(List<QuestionResponse> v) { this.questions = v; return this; }

        public ExamSessionResponse build() {
            ExamSessionResponse o = new ExamSessionResponse();
            o.setId(this.id);
            o.setCreatedAt(this.createdAt);
            o.setUpdatedAt(this.updatedAt);
            o.setCreatedBy(this.createdBy);
            o.setUpdatedBy(this.updatedBy);
            o.examId = this.examId;
            o.examTitle = this.examTitle;
            o.certificationId = this.certificationId;
            o.certificationName = this.certificationName;
            o.mode = this.mode;
            o.status = this.status;
            o.startTime = this.startTime;
            o.endTime = this.endTime;
            o.durationMinutes = this.durationMinutes;
            o.passingScore = this.passingScore;
            o.totalQuestions = this.totalQuestions;
            o.currentQuestionIndex = this.currentQuestionIndex;
            o.questions = this.questions;
            return o;
        }
    }
}
