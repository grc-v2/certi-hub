package com.certimaster.learning.adapter.web.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class FlashcardQuestionRequest {

    @NotNull(message = "Certification ID is required")
    private Long certificationId;

    private List<Long> domainIds;

    private List<String> difficulties;

    @Min(value = 1, message = "Count must be at least 1")
    @Max(value = 100, message = "Count must be at most 100")
    private Integer count;

    public FlashcardQuestionRequest() {
    }

    public Long getCertificationId() { return certificationId; }
    public void setCertificationId(Long certificationId) { this.certificationId = certificationId; }

    public List<Long> getDomainIds() { return domainIds; }
    public void setDomainIds(List<Long> domainIds) { this.domainIds = domainIds; }

    public List<String> getDifficulties() { return difficulties; }
    public void setDifficulties(List<String> difficulties) { this.difficulties = difficulties; }

    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
}
