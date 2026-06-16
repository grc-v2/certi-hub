package com.certimaster.engagement.application.listener;

import com.certimaster.engagement.application.command.CreateNotificationCommand;
import com.certimaster.engagement.application.command.NotificationCommandService;
import com.certimaster.learning.domain.event.ExamCompletedEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class ExamActivityRecorder {

    private final NotificationCommandService notificationCommandService;

    public ExamActivityRecorder(NotificationCommandService notificationCommandService) {
        this.notificationCommandService = notificationCommandService;
    }

    @ApplicationModuleListener
    public void onExamCompleted(ExamCompletedEvent event) {
        if (event.getUserId() == null) {
            return;
        }
        String examTitle = event.getExamTitle() != null ? event.getExamTitle() : "bài thi";
        String message = buildMessage(examTitle, event.getCorrectCount(), event.getTotalQuestions());
        String relatedEntityId = event.getSessionId() != null ? String.valueOf(event.getSessionId()) : null;

        notificationCommandService.create(new CreateNotificationCommand(
                event.getUserId(), "EXAM_RESULT", "Hoàn thành bài thi", message, relatedEntityId));
    }

    private String buildMessage(String examTitle, Integer correct, Integer total) {
        if (correct != null && total != null && total > 0) {
            return "Bạn đã hoàn thành \"" + examTitle + "\" với " + correct + "/" + total + " câu đúng.";
        }
        return "Bạn đã hoàn thành \"" + examTitle + "\".";
    }
}
