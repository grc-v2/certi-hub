package com.certimaster.learning.adapter.web.response;

import com.certimaster.shared.dto.BaseResponse;
import com.certimaster.learning.adapter.web.response.QuestionOptionResponse;

import java.time.LocalDateTime;
import java.util.List;

public class QuestionResponse extends BaseResponse {

    private String type;
    private String content;
    private String explanation;
    private String difficulty;
    private Integer points;
    private Integer timeLimitSeconds;
    private String referenceUrl;
    private Long domainId;
    private String domainName;
    private Long certificationId;
    private String certificationCode;
    private String status;
    private String questionCode;
    private LocalDateTime publishedAt;
    private List<QuestionOptionResponse> options;

    public QuestionResponse() {
    }

    public QuestionResponse(Long id, LocalDateTime createdAt, LocalDateTime updatedAt,
                             String createdBy, String updatedBy,
                             String type, String content, String explanation, String difficulty,
                             Integer points, Integer timeLimitSeconds, String referenceUrl,
                             Long domainId, List<QuestionOptionResponse> options) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.type = type;
        this.content = content;
        this.explanation = explanation;
        this.difficulty = difficulty;
        this.points = points;
        this.timeLimitSeconds = timeLimitSeconds;
        this.referenceUrl = referenceUrl;
        this.domainId = domainId;
        this.options = options;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public void setTimeLimitSeconds(Integer timeLimitSeconds) {
        this.timeLimitSeconds = timeLimitSeconds;
    }

    public String getReferenceUrl() {
        return referenceUrl;
    }

    public void setReferenceUrl(String referenceUrl) {
        this.referenceUrl = referenceUrl;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Long getCertificationId() {
        return certificationId;
    }

    public void setCertificationId(Long certificationId) {
        this.certificationId = certificationId;
    }

    public String getCertificationCode() {
        return certificationCode;
    }

    public void setCertificationCode(String certificationCode) {
        this.certificationCode = certificationCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuestionCode() {
        return questionCode;
    }

    public void setQuestionCode(String questionCode) {
        this.questionCode = questionCode;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public List<QuestionOptionResponse> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOptionResponse> options) {
        this.options = options;
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
        private String type;
        private String content;
        private String explanation;
        private String difficulty;
        private Integer points;
        private Integer timeLimitSeconds;
        private String referenceUrl;
        private Long domainId;
        private String domainName;
        private Long certificationId;
        private String certificationCode;
        private String status;
        private String questionCode;
        private LocalDateTime publishedAt;
        private List<QuestionOptionResponse> options;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public Builder createdBy(String v) { this.createdBy = v; return this; }
        public Builder updatedBy(String v) { this.updatedBy = v; return this; }
        public Builder type(String v) { this.type = v; return this; }
        public Builder content(String v) { this.content = v; return this; }
        public Builder explanation(String v) { this.explanation = v; return this; }
        public Builder difficulty(String v) { this.difficulty = v; return this; }
        public Builder points(Integer v) { this.points = v; return this; }
        public Builder timeLimitSeconds(Integer v) { this.timeLimitSeconds = v; return this; }
        public Builder referenceUrl(String v) { this.referenceUrl = v; return this; }
        public Builder domainId(Long v) { this.domainId = v; return this; }
        public Builder domainName(String v) { this.domainName = v; return this; }
        public Builder certificationId(Long v) { this.certificationId = v; return this; }
        public Builder certificationCode(String v) { this.certificationCode = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder questionCode(String v) { this.questionCode = v; return this; }
        public Builder publishedAt(LocalDateTime v) { this.publishedAt = v; return this; }
        public Builder options(List<QuestionOptionResponse> v) { this.options = v; return this; }

        public QuestionResponse build() {
            QuestionResponse o = new QuestionResponse();
            o.setId(this.id);
            o.setCreatedAt(this.createdAt);
            o.setUpdatedAt(this.updatedAt);
            o.setCreatedBy(this.createdBy);
            o.setUpdatedBy(this.updatedBy);
            o.type = this.type;
            o.content = this.content;
            o.explanation = this.explanation;
            o.difficulty = this.difficulty;
            o.points = this.points;
            o.timeLimitSeconds = this.timeLimitSeconds;
            o.referenceUrl = this.referenceUrl;
            o.domainId = this.domainId;
            o.domainName = this.domainName;
            o.certificationId = this.certificationId;
            o.certificationCode = this.certificationCode;
            o.status = this.status;
            o.questionCode = this.questionCode;
            o.publishedAt = this.publishedAt;
            o.options = this.options;
            return o;
        }
    }
}
