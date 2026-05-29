package com.certimaster.learning.adapter.web.mapper;

import com.certimaster.learning.application.command.CreateQuestionCommand;
import com.certimaster.learning.application.command.UpdateQuestionCommand;
import com.certimaster.learning.adapter.web.response.QuestionOptionResponse;
import com.certimaster.learning.adapter.web.response.QuestionResponse;
import com.certimaster.learning.domain.entity.Question;
import com.certimaster.learning.domain.entity.QuestionOption;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class QuestionMapper {

    public QuestionResponse toResponse(Question question) {
        if (question == null) {
            return null;
        }
        var domain = question.getDomain();
        Long domainId = domain != null ? domain.getId() : null;
        String domainName = domain != null ? domain.getName() : null;
        Long certificationId = null;
        String certificationCode = null;
        if (domain != null && domain.getCertification() != null) {
            certificationId = domain.getCertification().getId();
            certificationCode = domain.getCertification().getCode();
        }
        return QuestionResponse.builder()
                .id(question.getId())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .createdBy(question.getCreatedBy())
                .updatedBy(question.getUpdatedBy())
                .type(question.getType())
                .content(question.getContent())
                .explanation(question.getExplanation())
                .difficulty(question.getDifficulty())
                .points(question.getPoints())
                .timeLimitSeconds(question.getTimeLimitSeconds())
                .referenceUrl(question.getReferenceUrl())
                .domainId(domainId)
                .domainName(domainName)
                .certificationId(certificationId)
                .certificationCode(certificationCode)
                .status(question.getStatus())
                .questionCode(question.getQuestionCode())
                .publishedAt(question.getPublishedAt())
                .options(mapOptionsWithCorrect(question.getQuestionOptions()))
                .build();
    }

    public QuestionResponse toResponseWithoutCorrect(Question question) {
        if (question == null) {
            return null;
        }
        return QuestionResponse.builder()
                .id(question.getId())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .createdBy(question.getCreatedBy())
                .updatedBy(question.getUpdatedBy())
                .type(question.getType())
                .content(question.getContent())
                .difficulty(question.getDifficulty())
                .points(question.getPoints())
                .timeLimitSeconds(question.getTimeLimitSeconds())
                .domainId(question.getDomain() != null ? question.getDomain().getId() : null)
                .options(mapOptionsWithoutCorrect(question.getQuestionOptions()))
                .build();
    }

    public Question toEntity(CreateQuestionCommand command) {
        if (command == null) {
            return null;
        }
        return Question.builder()
                .type(command.type())
                .content(command.content())
                .explanation(command.explanation())
                .difficulty(command.difficulty())
                .points(command.points())
                .timeLimitSeconds(command.timeLimitSeconds())
                .referenceUrl(command.referenceUrl())
                .build();
    }

    public void updateEntity(Question question, UpdateQuestionCommand command) {
        if (question == null || command == null) {
            return;
        }
        if (command.type() != null) {
            question.setType(command.type());
        }
        if (command.content() != null) {
            question.setContent(command.content());
        }
        if (command.explanation() != null) {
            question.setExplanation(command.explanation());
        }
        if (command.difficulty() != null) {
            question.setDifficulty(command.difficulty());
        }
        if (command.points() != null) {
            question.setPoints(command.points());
        }
        if (command.timeLimitSeconds() != null) {
            question.setTimeLimitSeconds(command.timeLimitSeconds());
        }
        if (command.referenceUrl() != null) {
            question.setReferenceUrl(command.referenceUrl());
        }
    }

    private List<QuestionOptionResponse> mapOptionsWithCorrect(Set<QuestionOption> options) {
        if (options == null || options.isEmpty()) {
            return Collections.emptyList();
        }
        return options.stream()
                .filter(Objects::nonNull)
                .map(option -> QuestionOptionResponse.builder()
                        .id(option.getId())
                        .content(option.getContent())
                        .orderIndex(option.getOrderIndex())
                        .isCorrect(option.getIsCorrect())
                        .build())
                .collect(Collectors.toList());
    }

    private List<QuestionOptionResponse> mapOptionsWithoutCorrect(Set<QuestionOption> options) {
        if (options == null || options.isEmpty()) {
            return Collections.emptyList();
        }
        return options.stream()
                .filter(Objects::nonNull)
                .map(option -> QuestionOptionResponse.builder()
                        .id(option.getId())
                        .content(option.getContent())
                        .orderIndex(option.getOrderIndex())
                        .isCorrect(null)
                        .build())
                .collect(Collectors.toList());
    }
}
