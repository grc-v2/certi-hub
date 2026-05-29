package com.certimaster.learning.adapter.web.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public class QuestionWithOptionsRequest {

    @NotNull(message = "Domain ID is required")
    private Long domainId;

    @NotBlank(message = "Question type is required")
    @Pattern(regexp = "^(SINGLE_CHOICE|MULTIPLE_CHOICE|TRUE_FALSE)$",
            message = "Type must be one of: SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE")
    private String type;

    @NotBlank(message = "Question content is required")
    @Size(max = 10000, message = "Content must not exceed 10000 characters")
    private String content;

    @Size(max = 10000, message = "Explanation must not exceed 10000 characters")
    private String explanation;

    @Pattern(regexp = "^(EASY|MEDIUM|HARD)$",
            message = "Difficulty must be one of: EASY, MEDIUM, HARD")
    private String difficulty;

    @Min(value = 1, message = "Points must be at least 1")
    @Max(value = 100, message = "Points must not exceed 100")
    private Integer points;

    @Min(value = 0, message = "Time limit must be non-negative")
    @Max(value = 3600, message = "Time limit must not exceed 3600 seconds")
    private Integer timeLimitSeconds;

    @Size(max = 500, message = "Reference URL must not exceed 500 characters")
    private String referenceUrl;

    @Valid
    @NotEmpty(message = "At least one option is required")
    @Size(min = 2, max = 10, message = "Question must have between 2 and 10 options")
    private List<QuestionOptionRequest> options;

    public QuestionWithOptionsRequest() {
    }

    public QuestionWithOptionsRequest(Long domainId, String type, String content, String explanation,
                                       String difficulty, Integer points, Integer timeLimitSeconds,
                                       String referenceUrl, List<QuestionOptionRequest> options) {
        this.domainId = domainId;
        this.type = type;
        this.content = content;
        this.explanation = explanation;
        this.difficulty = difficulty;
        this.points = points;
        this.timeLimitSeconds = timeLimitSeconds;
        this.referenceUrl = referenceUrl;
        this.options = options;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
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

    public List<QuestionOptionRequest> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOptionRequest> options) {
        this.options = options;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long domainId;
        private String type;
        private String content;
        private String explanation;
        private String difficulty;
        private Integer points;
        private Integer timeLimitSeconds;
        private String referenceUrl;
        private List<QuestionOptionRequest> options;

        public Builder domainId(Long v) { this.domainId = v; return this; }
        public Builder type(String v) { this.type = v; return this; }
        public Builder content(String v) { this.content = v; return this; }
        public Builder explanation(String v) { this.explanation = v; return this; }
        public Builder difficulty(String v) { this.difficulty = v; return this; }
        public Builder points(Integer v) { this.points = v; return this; }
        public Builder timeLimitSeconds(Integer v) { this.timeLimitSeconds = v; return this; }
        public Builder referenceUrl(String v) { this.referenceUrl = v; return this; }
        public Builder options(List<QuestionOptionRequest> v) { this.options = v; return this; }

        public QuestionWithOptionsRequest build() {
            QuestionWithOptionsRequest o = new QuestionWithOptionsRequest();
            o.domainId = this.domainId;
            o.type = this.type;
            o.content = this.content;
            o.explanation = this.explanation;
            o.difficulty = this.difficulty;
            o.points = this.points;
            o.timeLimitSeconds = this.timeLimitSeconds;
            o.referenceUrl = this.referenceUrl;
            o.options = this.options;
            return o;
        }
    }
}
