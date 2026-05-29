package com.certimaster.learning.adapter.web.response;

import java.time.LocalDateTime;
import java.util.List;

public class ExamDetailResponse extends ExamResponse {

    private List<ExamQuestionResponse> examQuestions;

    public ExamDetailResponse() {
    }

    public ExamDetailResponse(Long id, LocalDateTime createdAt, LocalDateTime updatedAt,
                               String createdBy, String updatedBy,
                               String title, String type, String description, Integer durationMinutes,
                               Integer totalQuestions, Integer passingScore, String status,
                               Long certificationId, String certificationName,
                               List<ExamQuestionResponse> examQuestions) {
        super(id, createdAt, updatedAt, createdBy, updatedBy, title, type, description,
                durationMinutes, totalQuestions, passingScore, status, certificationId, certificationName);
        this.examQuestions = examQuestions;
    }

    public List<ExamQuestionResponse> getExamQuestions() {
        return examQuestions;
    }

    public void setExamQuestions(List<ExamQuestionResponse> examQuestions) {
        this.examQuestions = examQuestions;
    }

    public static Builder detailBuilder() {
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
        private List<ExamQuestionResponse> examQuestions;

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
        public Builder examQuestions(List<ExamQuestionResponse> v) { this.examQuestions = v; return this; }

        public ExamDetailResponse build() {
            ExamDetailResponse o = new ExamDetailResponse();
            o.setId(this.id);
            o.setCreatedAt(this.createdAt);
            o.setUpdatedAt(this.updatedAt);
            o.setCreatedBy(this.createdBy);
            o.setUpdatedBy(this.updatedBy);
            o.setTitle(this.title);
            o.setType(this.type);
            o.setDescription(this.description);
            o.setDurationMinutes(this.durationMinutes);
            o.setTotalQuestions(this.totalQuestions);
            o.setPassingScore(this.passingScore);
            o.setStatus(this.status);
            o.setCertificationId(this.certificationId);
            o.setCertificationName(this.certificationName);
            o.examQuestions = this.examQuestions;
            return o;
        }
    }
}
