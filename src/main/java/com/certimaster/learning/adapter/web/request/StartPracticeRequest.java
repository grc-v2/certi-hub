package com.certimaster.learning.adapter.web.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class StartPracticeRequest {

    @NotNull(message = "Certification ID is required")
    private Long certificationId;

    private Long questionBankId;

    private List<Long> domainIds;

    private List<String> difficulties;

    @NotNull(message = "Question count is required")
    @Min(value = 1, message = "Question count must be at least 1")
    @Max(value = 200, message = "Question count must be at most 200")
    private Integer questionCount;

    public StartPracticeRequest() {
    }

    public Long getCertificationId() { return certificationId; }
    public void setCertificationId(Long certificationId) { this.certificationId = certificationId; }

    public Long getQuestionBankId() { return questionBankId; }
    public void setQuestionBankId(Long questionBankId) { this.questionBankId = questionBankId; }

    public List<Long> getDomainIds() { return domainIds; }
    public void setDomainIds(List<Long> domainIds) { this.domainIds = domainIds; }

    public List<String> getDifficulties() { return difficulties; }
    public void setDifficulties(List<String> difficulties) { this.difficulties = difficulties; }

    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long certificationId;
        private Long questionBankId;
        private List<Long> domainIds;
        private List<String> difficulties;
        private Integer questionCount;

        public Builder certificationId(Long v) { this.certificationId = v; return this; }
        public Builder questionBankId(Long v) { this.questionBankId = v; return this; }
        public Builder domainIds(List<Long> v) { this.domainIds = v; return this; }
        public Builder difficulties(List<String> v) { this.difficulties = v; return this; }
        public Builder questionCount(Integer v) { this.questionCount = v; return this; }

        public StartPracticeRequest build() {
            StartPracticeRequest o = new StartPracticeRequest();
            o.certificationId = this.certificationId;
            o.questionBankId = this.questionBankId;
            o.domainIds = this.domainIds;
            o.difficulties = this.difficulties;
            o.questionCount = this.questionCount;
            return o;
        }
    }
}
