package com.certimaster.engagement.application.listener;

import com.certimaster.learning.domain.event.ExamCompletedEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class ExamActivityRecorder {


    @ApplicationModuleListener
    public void onExamCompleted(ExamCompletedEvent event) {
    }
}
