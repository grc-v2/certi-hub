package com.certimaster.learning.domain.entity;

import com.certimaster.shared.entity.BaseEntity;
import com.certimaster.learning.domain.entity.Certification;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exams")
public class Exam extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    private Certification certification;

    @Column(name = "title")
    private String title;

    @Column(name = "type", length = 50)
    private String type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "passing_score")
    private Integer passingScore;

    @Column(name = "status", length = 20)
    private String status;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ExamQuestion> examQuestions = new HashSet<>();

    public Exam() {
        super();
    }

    public Exam(Certification certification, String title, String type, String description,
                Integer durationMinutes, Integer totalQuestions, Integer passingScore,
                String status, Set<ExamQuestion> examQuestions) {
        super();
        this.certification = certification;
        this.title = title;
        this.type = type;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.totalQuestions = totalQuestions;
        this.passingScore = passingScore;
        this.status = status;
        this.examQuestions = examQuestions;
    }

    public Certification getCertification() { return certification; }
    public void setCertification(Certification certification) { this.certification = certification; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public Integer getPassingScore() { return passingScore; }
    public void setPassingScore(Integer passingScore) { this.passingScore = passingScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Set<ExamQuestion> getExamQuestions() { return examQuestions; }
    public void setExamQuestions(Set<ExamQuestion> examQuestions) { this.examQuestions = examQuestions; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Certification certification;
        private String title;
        private String type;
        private String description;
        private Integer durationMinutes;
        private Integer totalQuestions;
        private Integer passingScore;
        private String status;
        private Set<ExamQuestion> examQuestions = new HashSet<>();

        public Builder certification(Certification v) { this.certification = v; return this; }
        public Builder title(String v) { this.title = v; return this; }
        public Builder type(String v) { this.type = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder durationMinutes(Integer v) { this.durationMinutes = v; return this; }
        public Builder totalQuestions(Integer v) { this.totalQuestions = v; return this; }
        public Builder passingScore(Integer v) { this.passingScore = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder examQuestions(Set<ExamQuestion> v) { this.examQuestions = v; return this; }

        public Exam build() {
            Exam o = new Exam();
            o.certification = this.certification;
            o.title = this.title;
            o.type = this.type;
            o.description = this.description;
            o.durationMinutes = this.durationMinutes;
            o.totalQuestions = this.totalQuestions;
            o.passingScore = this.passingScore;
            o.status = this.status;
            o.examQuestions = this.examQuestions;
            return o;
        }
    }
}
