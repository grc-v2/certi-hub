package com.certimaster.learning.adapter.web.mapper;

import com.certimaster.learning.application.command.CreateExamCommand;
import com.certimaster.learning.application.command.UpdateExamCommand;
import com.certimaster.learning.adapter.web.response.ExamDetailResponse;
import com.certimaster.learning.adapter.web.response.ExamQuestionResponse;
import com.certimaster.learning.adapter.web.response.ExamResponse;
import com.certimaster.learning.domain.entity.Exam;
import com.certimaster.learning.domain.entity.ExamQuestion;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ExamMapper {

    public ExamResponse toResponse(Exam exam) {
        if (exam == null) {
            return null;
        }
        return ExamResponse.builder()
                .id(exam.getId())
                .createdAt(exam.getCreatedAt())
                .updatedAt(exam.getUpdatedAt())
                .createdBy(exam.getCreatedBy())
                .updatedBy(exam.getUpdatedBy())
                .title(exam.getTitle())
                .type(exam.getType())
                .description(exam.getDescription())
                .durationMinutes(exam.getDurationMinutes())
                .totalQuestions(exam.getTotalQuestions())
                .passingScore(exam.getPassingScore())
                .status(exam.getStatus())
                .certificationId(exam.getCertification() != null ? exam.getCertification().getId() : null)
                .certificationName(exam.getCertification() != null ? exam.getCertification().getName() : null)
                .build();
    }

    public ExamDetailResponse toDetailResponse(Exam exam) {
        if (exam == null) {
            return null;
        }
        
        List<ExamQuestionResponse> examQuestions = mapExamQuestions(exam);
        
        return ExamDetailResponse.detailBuilder()
                .id(exam.getId())
                .createdAt(exam.getCreatedAt())
                .updatedAt(exam.getUpdatedAt())
                .createdBy(exam.getCreatedBy())
                .updatedBy(exam.getUpdatedBy())
                .title(exam.getTitle())
                .type(exam.getType())
                .description(exam.getDescription())
                .durationMinutes(exam.getDurationMinutes())
                .totalQuestions(exam.getTotalQuestions())
                .passingScore(exam.getPassingScore())
                .status(exam.getStatus())
                .certificationId(exam.getCertification() != null ? exam.getCertification().getId() : null)
                .certificationName(exam.getCertification() != null ? exam.getCertification().getName() : null)
                .examQuestions(examQuestions)
                .build();
    }

    public Exam toEntity(CreateExamCommand command) {
        if (command == null) {
            return null;
        }
        return Exam.builder()
                .title(command.title())
                .type(command.type())
                .description(command.description())
                .durationMinutes(command.durationMinutes())
                .totalQuestions(command.totalQuestions())
                .passingScore(command.passingScore())
                .status(command.status())
                .build();
    }

    public void updateEntity(Exam exam, UpdateExamCommand command) {
        if (exam == null || command == null) {
            return;
        }
        if (command.title() != null) {
            exam.setTitle(command.title());
        }
        if (command.type() != null) {
            exam.setType(command.type());
        }
        if (command.description() != null) {
            exam.setDescription(command.description());
        }
        if (command.durationMinutes() != null) {
            exam.setDurationMinutes(command.durationMinutes());
        }
        if (command.totalQuestions() != null) {
            exam.setTotalQuestions(command.totalQuestions());
        }
        if (command.passingScore() != null) {
            exam.setPassingScore(command.passingScore());
        }
        if (command.status() != null) {
            exam.setStatus(command.status());
        }
    }

    public ExamQuestionResponse toExamQuestionResponse(ExamQuestion examQuestion) {
        if (examQuestion == null) {
            return null;
        }
        return ExamQuestionResponse.builder()
                .id(examQuestion.getId())
                .createdAt(examQuestion.getCreatedAt())
                .updatedAt(examQuestion.getUpdatedAt())
                .createdBy(examQuestion.getCreatedBy())
                .updatedBy(examQuestion.getUpdatedBy())
                .examId(examQuestion.getExam() != null ? examQuestion.getExam().getId() : null)
                .questionId(examQuestion.getQuestion() != null ? examQuestion.getQuestion().getId() : null)
                .orderIndex(examQuestion.getOrderIndex())
                .build();
    }

    private List<ExamQuestionResponse> mapExamQuestions(Exam exam) {
        if (exam.getExamQuestions() == null || exam.getExamQuestions().isEmpty()) {
            return Collections.emptyList();
        }
        return exam.getExamQuestions().stream()
                .filter(Objects::nonNull)
                .map(this::toExamQuestionResponse)
                .collect(Collectors.toList());
    }
}
