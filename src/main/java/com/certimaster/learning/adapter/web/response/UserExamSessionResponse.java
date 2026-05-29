package com.certimaster.learning.adapter.web.response;

import com.certimaster.shared.dto.BaseResponse;

import java.time.LocalDateTime;

public class UserExamSessionResponse extends BaseResponse {

    private Long userId;
    private Long examId;
    private Long certificationId;
    private String certificationName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String mode;
    private String examTitle;
    private Integer totalQuestions;
    private Integer durationMinutes;
    private Integer answeredCount;
    private Integer correctCount;
    private Integer wrongCount;
    private Integer unansweredCount;
    private Integer flaggedCount;
    private Integer timeSpentSeconds;

    public UserExamSessionResponse() {
    }

    public UserExamSessionResponse(Long id, LocalDateTime createdAt, LocalDateTime updatedAt,
                                    String createdBy, String updatedBy,
                                    Long userId, Long examId, Long certificationId, String certificationName,
                                    LocalDateTime startTime, LocalDateTime endTime, String status, String mode,
                                    String examTitle, Integer totalQuestions, Integer durationMinutes,
                                    Integer answeredCount, Integer correctCount, Integer wrongCount,
                                    Integer unansweredCount, Integer flaggedCount, Integer timeSpentSeconds) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.userId = userId;
        this.examId = examId;
        this.certificationId = certificationId;
        this.certificationName = certificationName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.mode = mode;
        this.examTitle = examTitle;
        this.totalQuestions = totalQuestions;
        this.durationMinutes = durationMinutes;
        this.answeredCount = answeredCount;
        this.correctCount = correctCount;
        this.wrongCount = wrongCount;
        this.unansweredCount = unansweredCount;
        this.flaggedCount = flaggedCount;
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getAnsweredCount() {
        return answeredCount;
    }

    public void setAnsweredCount(Integer answeredCount) {
        this.answeredCount = answeredCount;
    }

    public Integer getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }

    public Integer getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(Integer wrongCount) {
        this.wrongCount = wrongCount;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Long userId;
        private Long examId;
        private Long certificationId;
        private String certificationName;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status;
        private String mode;
        private String examTitle;
        private Integer totalQuestions;
        private Integer durationMinutes;
        private Integer answeredCount;
        private Integer correctCount;
        private Integer wrongCount;
        private Integer unansweredCount;
        private Integer flaggedCount;
        private Integer timeSpentSeconds;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public Builder createdBy(String v) { this.createdBy = v; return this; }
        public Builder updatedBy(String v) { this.updatedBy = v; return this; }
        public Builder userId(Long v) { this.userId = v; return this; }
        public Builder examId(Long v) { this.examId = v; return this; }
        public Builder certificationId(Long v) { this.certificationId = v; return this; }
        public Builder certificationName(String v) { this.certificationName = v; return this; }
        public Builder startTime(LocalDateTime v) { this.startTime = v; return this; }
        public Builder endTime(LocalDateTime v) { this.endTime = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder mode(String v) { this.mode = v; return this; }
        public Builder examTitle(String v) { this.examTitle = v; return this; }
        public Builder totalQuestions(Integer v) { this.totalQuestions = v; return this; }
        public Builder durationMinutes(Integer v) { this.durationMinutes = v; return this; }
        public Builder answeredCount(Integer v) { this.answeredCount = v; return this; }
        public Builder correctCount(Integer v) { this.correctCount = v; return this; }
        public Builder wrongCount(Integer v) { this.wrongCount = v; return this; }
        public Builder unansweredCount(Integer v) { this.unansweredCount = v; return this; }
        public Builder flaggedCount(Integer v) { this.flaggedCount = v; return this; }
        public Builder timeSpentSeconds(Integer v) { this.timeSpentSeconds = v; return this; }

        public UserExamSessionResponse build() {
            UserExamSessionResponse o = new UserExamSessionResponse();
            o.setId(this.id);
            o.setCreatedAt(this.createdAt);
            o.setUpdatedAt(this.updatedAt);
            o.setCreatedBy(this.createdBy);
            o.setUpdatedBy(this.updatedBy);
            o.userId = this.userId;
            o.examId = this.examId;
            o.certificationId = this.certificationId;
            o.certificationName = this.certificationName;
            o.startTime = this.startTime;
            o.endTime = this.endTime;
            o.status = this.status;
            o.mode = this.mode;
            o.examTitle = this.examTitle;
            o.totalQuestions = this.totalQuestions;
            o.durationMinutes = this.durationMinutes;
            o.answeredCount = this.answeredCount;
            o.correctCount = this.correctCount;
            o.wrongCount = this.wrongCount;
            o.unansweredCount = this.unansweredCount;
            o.flaggedCount = this.flaggedCount;
            o.timeSpentSeconds = this.timeSpentSeconds;
            return o;
        }
    }
}
