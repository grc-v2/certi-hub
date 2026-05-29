package com.certimaster.learning.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import com.certimaster.learning.domain.entity.CertificationDomain;
import com.certimaster.learning.domain.entity.ExamQuestion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "questions")
public class Question extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_id", nullable = false)
    private CertificationDomain domain;

    @Column(name = "type", length = 50, nullable = false)
    private String type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "difficulty", length = 20)
    private String difficulty;

    @Column(name = "points")
    private Integer points;

    @Column(name = "time_limit_seconds")
    private Integer timeLimitSeconds;

    @Column(name = "reference_url", length = 500)
    private String referenceUrl;

    @Column(name = "question_code", length = 50, unique = true)
    private String questionCode;

    @Column(name = "scenario", columnDefinition = "TEXT")
    private String scenario;

    @Column(name = "question_media", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> questionMedia;

    @Column(name = "wrong_answer_explanations", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> wrongAnswerExplanations;

    @Column(name = "reference_urls", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> referenceUrls;

    @Column(name = "estimated_time_seconds")
    private Integer estimatedTimeSeconds;

    @Column(name = "status", length = 20)
    private String status = "PUBLISHED";

    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes;

    @Column(name = "reviewed_by", length = 50)
    private String reviewedBy;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<QuestionOption> questionOptions = new HashSet<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ExamQuestion> examQuestions = new HashSet<>();

    public Question() {
        super();
    }

    public CertificationDomain getDomain() { return domain; }
    public void setDomain(CertificationDomain domain) { this.domain = domain; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public Integer getTimeLimitSeconds() { return timeLimitSeconds; }
    public void setTimeLimitSeconds(Integer timeLimitSeconds) { this.timeLimitSeconds = timeLimitSeconds; }

    public String getReferenceUrl() { return referenceUrl; }
    public void setReferenceUrl(String referenceUrl) { this.referenceUrl = referenceUrl; }

    public String getQuestionCode() { return questionCode; }
    public void setQuestionCode(String questionCode) { this.questionCode = questionCode; }

    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }

    public Map<String, Object> getQuestionMedia() { return questionMedia; }
    public void setQuestionMedia(Map<String, Object> questionMedia) { this.questionMedia = questionMedia; }

    public Map<String, String> getWrongAnswerExplanations() { return wrongAnswerExplanations; }
    public void setWrongAnswerExplanations(Map<String, String> wrongAnswerExplanations) { this.wrongAnswerExplanations = wrongAnswerExplanations; }

    public List<String> getReferenceUrls() { return referenceUrls; }
    public void setReferenceUrls(List<String> referenceUrls) { this.referenceUrls = referenceUrls; }

    public Integer getEstimatedTimeSeconds() { return estimatedTimeSeconds; }
    public void setEstimatedTimeSeconds(Integer estimatedTimeSeconds) { this.estimatedTimeSeconds = estimatedTimeSeconds; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReviewNotes() { return reviewNotes; }
    public void setReviewNotes(String reviewNotes) { this.reviewNotes = reviewNotes; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public Set<QuestionOption> getQuestionOptions() { return questionOptions; }
    public void setQuestionOptions(Set<QuestionOption> questionOptions) { this.questionOptions = questionOptions; }

    public Set<ExamQuestion> getExamQuestions() { return examQuestions; }
    public void setExamQuestions(Set<ExamQuestion> examQuestions) { this.examQuestions = examQuestions; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private CertificationDomain domain;
        private String type;
        private String content;
        private String explanation;
        private String difficulty;
        private Integer points;
        private Integer timeLimitSeconds;
        private String referenceUrl;
        private String questionCode;
        private String scenario;
        private Map<String, Object> questionMedia;
        private Map<String, String> wrongAnswerExplanations;
        private List<String> referenceUrls;
        private Integer estimatedTimeSeconds;
        private String status = "PUBLISHED";
        private String reviewNotes;
        private String reviewedBy;
        private LocalDateTime publishedAt;
        private Set<QuestionOption> questionOptions = new HashSet<>();
        private Set<ExamQuestion> examQuestions = new HashSet<>();

        public Builder domain(CertificationDomain v) { this.domain = v; return this; }
        public Builder type(String v) { this.type = v; return this; }
        public Builder content(String v) { this.content = v; return this; }
        public Builder explanation(String v) { this.explanation = v; return this; }
        public Builder difficulty(String v) { this.difficulty = v; return this; }
        public Builder points(Integer v) { this.points = v; return this; }
        public Builder timeLimitSeconds(Integer v) { this.timeLimitSeconds = v; return this; }
        public Builder referenceUrl(String v) { this.referenceUrl = v; return this; }
        public Builder questionCode(String v) { this.questionCode = v; return this; }
        public Builder scenario(String v) { this.scenario = v; return this; }
        public Builder questionMedia(Map<String, Object> v) { this.questionMedia = v; return this; }
        public Builder wrongAnswerExplanations(Map<String, String> v) { this.wrongAnswerExplanations = v; return this; }
        public Builder referenceUrls(List<String> v) { this.referenceUrls = v; return this; }
        public Builder estimatedTimeSeconds(Integer v) { this.estimatedTimeSeconds = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder reviewNotes(String v) { this.reviewNotes = v; return this; }
        public Builder reviewedBy(String v) { this.reviewedBy = v; return this; }
        public Builder publishedAt(LocalDateTime v) { this.publishedAt = v; return this; }
        public Builder questionOptions(Set<QuestionOption> v) { this.questionOptions = v; return this; }
        public Builder examQuestions(Set<ExamQuestion> v) { this.examQuestions = v; return this; }

        public Question build() {
            Question o = new Question();
            o.domain = this.domain;
            o.type = this.type;
            o.content = this.content;
            o.explanation = this.explanation;
            o.difficulty = this.difficulty;
            o.points = this.points;
            o.timeLimitSeconds = this.timeLimitSeconds;
            o.referenceUrl = this.referenceUrl;
            o.questionCode = this.questionCode;
            o.scenario = this.scenario;
            o.questionMedia = this.questionMedia;
            o.wrongAnswerExplanations = this.wrongAnswerExplanations;
            o.referenceUrls = this.referenceUrls;
            o.estimatedTimeSeconds = this.estimatedTimeSeconds;
            o.status = this.status;
            o.reviewNotes = this.reviewNotes;
            o.reviewedBy = this.reviewedBy;
            o.publishedAt = this.publishedAt;
            o.questionOptions = this.questionOptions;
            o.examQuestions = this.examQuestions;
            return o;
        }
    }
}
