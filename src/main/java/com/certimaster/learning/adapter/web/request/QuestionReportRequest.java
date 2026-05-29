package com.certimaster.learning.adapter.web.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public class QuestionReportRequest {

    @Min(value = 0, message = "Page number must be non-negative")
    private Integer page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    private Integer size = 10;

    @Pattern(regexp = "^(ALL|CORRECT|WRONG|FLAGGED)$", message = "Filter must be one of: ALL, CORRECT, WRONG, FLAGGED")
    private String filter = "ALL";

    private Long topicId;

    @Pattern(regexp = "^(EASY|MEDIUM|HARD)$", message = "Difficulty must be one of: EASY, MEDIUM, HARD")
    private String difficulty;

    public QuestionReportRequest() {}

    public QuestionReportRequest(Integer page, Integer size, String filter, Long topicId, String difficulty) {
        this.page = page;
        this.size = size;
        this.filter = filter;
        this.topicId = topicId;
        this.difficulty = difficulty;
    }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public String getFilter() { return filter; }
    public void setFilter(String filter) { this.filter = filter; }

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Integer page = 0;
        private Integer size = 10;
        private String filter = "ALL";
        private Long topicId;
        private String difficulty;

        public Builder page(Integer v) { this.page = v; return this; }
        public Builder size(Integer v) { this.size = v; return this; }
        public Builder filter(String v) { this.filter = v; return this; }
        public Builder topicId(Long v) { this.topicId = v; return this; }
        public Builder difficulty(String v) { this.difficulty = v; return this; }

        public QuestionReportRequest build() {
            QuestionReportRequest o = new QuestionReportRequest();
            o.page = this.page;
            o.size = this.size;
            o.filter = this.filter;
            o.topicId = this.topicId;
            o.difficulty = this.difficulty;
            return o;
        }
    }
}
