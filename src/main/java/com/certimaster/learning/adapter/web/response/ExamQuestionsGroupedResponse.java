package com.certimaster.learning.adapter.web.response;

import com.certimaster.learning.adapter.web.response.QuestionOptionResponse;

import java.util.List;

public class ExamQuestionsGroupedResponse {

    private Long examId;
    private String examTitle;
    private Integer totalQuestions;
    private List<DomainQuestionGroup> domainGroups;

    public ExamQuestionsGroupedResponse() {}

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public String getExamTitle() { return examTitle; }
    public void setExamTitle(String examTitle) { this.examTitle = examTitle; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public List<DomainQuestionGroup> getDomainGroups() { return domainGroups; }
    public void setDomainGroups(List<DomainQuestionGroup> domainGroups) { this.domainGroups = domainGroups; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long examId;
        private String examTitle;
        private Integer totalQuestions;
        private List<DomainQuestionGroup> domainGroups;

        public Builder examId(Long v) { this.examId = v; return this; }
        public Builder examTitle(String v) { this.examTitle = v; return this; }
        public Builder totalQuestions(Integer v) { this.totalQuestions = v; return this; }
        public Builder domainGroups(List<DomainQuestionGroup> v) { this.domainGroups = v; return this; }

        public ExamQuestionsGroupedResponse build() {
            ExamQuestionsGroupedResponse o = new ExamQuestionsGroupedResponse();
            o.examId = this.examId;
            o.examTitle = this.examTitle;
            o.totalQuestions = this.totalQuestions;
            o.domainGroups = this.domainGroups;
            return o;
        }
    }

    public static class DomainQuestionGroup {

        private Long domainId;
        private String domainName;
        private String domainCode;
        private Integer questionCount;
        private List<QuestionInExam> questions;

        public DomainQuestionGroup() {}

        public Long getDomainId() { return domainId; }
        public void setDomainId(Long domainId) { this.domainId = domainId; }

        public String getDomainName() { return domainName; }
        public void setDomainName(String domainName) { this.domainName = domainName; }

        public String getDomainCode() { return domainCode; }
        public void setDomainCode(String domainCode) { this.domainCode = domainCode; }

        public Integer getQuestionCount() { return questionCount; }
        public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }

        public List<QuestionInExam> getQuestions() { return questions; }
        public void setQuestions(List<QuestionInExam> questions) { this.questions = questions; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Long domainId;
            private String domainName;
            private String domainCode;
            private Integer questionCount;
            private List<QuestionInExam> questions;

            public Builder domainId(Long v) { this.domainId = v; return this; }
            public Builder domainName(String v) { this.domainName = v; return this; }
            public Builder domainCode(String v) { this.domainCode = v; return this; }
            public Builder questionCount(Integer v) { this.questionCount = v; return this; }
            public Builder questions(List<QuestionInExam> v) { this.questions = v; return this; }

            public DomainQuestionGroup build() {
                DomainQuestionGroup o = new DomainQuestionGroup();
                o.domainId = this.domainId;
                o.domainName = this.domainName;
                o.domainCode = this.domainCode;
                o.questionCount = this.questionCount;
                o.questions = this.questions;
                return o;
            }
        }
    }

    public static class QuestionInExam {

        private Long questionId;
        private Integer orderIndex;
        private String type;
        private String content;
        private String difficulty;
        private Integer points;
        private Integer timeLimitSeconds;
        private List<QuestionOptionResponse> options;

        public QuestionInExam() {}

        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }

        public Integer getOrderIndex() { return orderIndex; }
        public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

        public Integer getPoints() { return points; }
        public void setPoints(Integer points) { this.points = points; }

        public Integer getTimeLimitSeconds() { return timeLimitSeconds; }
        public void setTimeLimitSeconds(Integer timeLimitSeconds) { this.timeLimitSeconds = timeLimitSeconds; }

        public List<QuestionOptionResponse> getOptions() { return options; }
        public void setOptions(List<QuestionOptionResponse> options) { this.options = options; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Long questionId;
            private Integer orderIndex;
            private String type;
            private String content;
            private String difficulty;
            private Integer points;
            private Integer timeLimitSeconds;
            private List<QuestionOptionResponse> options;

            public Builder questionId(Long v) { this.questionId = v; return this; }
            public Builder orderIndex(Integer v) { this.orderIndex = v; return this; }
            public Builder type(String v) { this.type = v; return this; }
            public Builder content(String v) { this.content = v; return this; }
            public Builder difficulty(String v) { this.difficulty = v; return this; }
            public Builder points(Integer v) { this.points = v; return this; }
            public Builder timeLimitSeconds(Integer v) { this.timeLimitSeconds = v; return this; }
            public Builder options(List<QuestionOptionResponse> v) { this.options = v; return this; }

            public QuestionInExam build() {
                QuestionInExam o = new QuestionInExam();
                o.questionId = this.questionId;
                o.orderIndex = this.orderIndex;
                o.type = this.type;
                o.content = this.content;
                o.difficulty = this.difficulty;
                o.points = this.points;
                o.timeLimitSeconds = this.timeLimitSeconds;
                o.options = this.options;
                return o;
            }
        }
    }
}
