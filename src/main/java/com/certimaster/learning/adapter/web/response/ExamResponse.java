package com.certimaster.learning.adapter.web.response;


import com.certimaster.shared.dto.BaseResponse;

import java.time.LocalDateTime;

public class ExamResponse extends BaseResponse {

    private String title;
    private String type;
    private String description;
    private Integer durationMinutes;
    private Integer totalQuestions;
    private Integer passingScore;
    private String status;
    private Long certificationId;
    private String certificationName;

    public ExamResponse() {
    }

    public ExamResponse(Long id, LocalDateTime createdAt, LocalDateTime updatedAt,
                        String createdBy, String updatedBy,
                        String title, String type, String description, Integer durationMinutes,
                        Integer totalQuestions, Integer passingScore, String status,
                        Long certificationId, String certificationName) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.title = title;
        this.type = type;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.totalQuestions = totalQuestions;
        this.passingScore = passingScore;
        this.status = status;
        this.certificationId = certificationId;
        this.certificationName = certificationName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(Integer passingScore) {
        this.passingScore = passingScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private String title;
        private String type;
        private String description;
        private Integer durationMinutes;
        private Integer totalQuestions;
        private Integer passingScore;
        private String status;
        private Long certificationId;
        private String certificationName;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public Builder createdBy(String v) { this.createdBy = v; return this; }
        public Builder updatedBy(String v) { this.updatedBy = v; return this; }
        public Builder title(String v) { this.title = v; return this; }
        public Builder type(String v) { this.type = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder durationMinutes(Integer v) { this.durationMinutes = v; return this; }
        public Builder totalQuestions(Integer v) { this.totalQuestions = v; return this; }
        public Builder passingScore(Integer v) { this.passingScore = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder certificationId(Long v) { this.certificationId = v; return this; }
        public Builder certificationName(String v) { this.certificationName = v; return this; }

        public ExamResponse build() {
            ExamResponse o = new ExamResponse();
            o.setId(this.id);
            o.setCreatedAt(this.createdAt);
            o.setUpdatedAt(this.updatedAt);
            o.setCreatedBy(this.createdBy);
            o.setUpdatedBy(this.updatedBy);
            o.title = this.title;
            o.type = this.type;
            o.description = this.description;
            o.durationMinutes = this.durationMinutes;
            o.totalQuestions = this.totalQuestions;
            o.passingScore = this.passingScore;
            o.status = this.status;
            o.certificationId = this.certificationId;
            o.certificationName = this.certificationName;
            return o;
        }
    }
}
