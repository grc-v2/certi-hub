package com.certimaster.learning.adapter.web.response;

import com.fasterxml.jackson.annotation.JsonInclude;

public class QuestionOptionResponse {

    private Long id;
    private String content;
    private Integer orderIndex;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isCorrect;

    public QuestionOptionResponse() {
    }

    public QuestionOptionResponse(Long id, String content, Integer orderIndex, Boolean isCorrect) {
        this.id = id;
        this.content = content;
        this.orderIndex = orderIndex;
        this.isCorrect = isCorrect;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String content;
        private Integer orderIndex;
        private Boolean isCorrect;

        public Builder id(Long v) { this.id = v; return this; }
        public Builder content(String v) { this.content = v; return this; }
        public Builder orderIndex(Integer v) { this.orderIndex = v; return this; }
        public Builder isCorrect(Boolean v) { this.isCorrect = v; return this; }

        public QuestionOptionResponse build() {
            QuestionOptionResponse o = new QuestionOptionResponse();
            o.id = this.id;
            o.content = this.content;
            o.orderIndex = this.orderIndex;
            o.isCorrect = this.isCorrect;
            return o;
        }
    }
}
