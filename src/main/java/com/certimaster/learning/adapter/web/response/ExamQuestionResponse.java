package com.certimaster.learning.adapter.web.response;

import com.certimaster.shared.dto.BaseResponse;

import java.time.LocalDateTime;

public class ExamQuestionResponse extends BaseResponse {

    private Long examId;
    private Long questionId;
    private Integer orderIndex;

    public ExamQuestionResponse() {
    }

    public ExamQuestionResponse(Long id, LocalDateTime createdAt, LocalDateTime updatedAt,
                                 String createdBy, String updatedBy,
                                 Long examId, Long questionId, Integer orderIndex) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.examId = examId;
        this.questionId = questionId;
        this.orderIndex = orderIndex;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
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
        private Long questionId;
        private Integer orderIndex;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public Builder createdBy(String v) { this.createdBy = v; return this; }
        public Builder updatedBy(String v) { this.updatedBy = v; return this; }
        public Builder examId(Long v) { this.examId = v; return this; }
        public Builder questionId(Long v) { this.questionId = v; return this; }
        public Builder orderIndex(Integer v) { this.orderIndex = v; return this; }

        public ExamQuestionResponse build() {
            ExamQuestionResponse o = new ExamQuestionResponse();
            o.setId(this.id);
            o.setCreatedAt(this.createdAt);
            o.setUpdatedAt(this.updatedAt);
            o.setCreatedBy(this.createdBy);
            o.setUpdatedBy(this.updatedBy);
            o.examId = this.examId;
            o.questionId = this.questionId;
            o.orderIndex = this.orderIndex;
            return o;
        }
    }
}
