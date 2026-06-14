package com.certimaster.learning.application.command;

import com.certimaster.learning.adapter.web.response.ExamResultResponse;
import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.domain.event.ResultCreatedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
class ResultCreatedEventFactory {

    ResultCreatedEvent create(UserExamSession session, ExamResultResponse result) {
        return new ResultCreatedEvent(
                UUID.randomUUID().toString(),
                result.getResultId(),
                session.getId(),
                session.getUserId(),
                session.getExam() != null ? session.getExam().getId() : null,
                session.getCertification() != null ? session.getCertification().getId() : null,
                session.getPassed(),
                result.getPercentage(),
                result.getTimeTakenSeconds(),
                LocalDateTime.now().toInstant(ZoneOffset.UTC)
        );
    }
}
