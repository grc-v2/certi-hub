package com.certimaster.engagement.application.listener;

import com.certimaster.learning.domain.event.ExamCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class ExamActivityRecorder {

    private static final Logger log = LoggerFactory.getLogger(ExamActivityRecorder.class);

    @ApplicationModuleListener
    public void onExamCompleted(ExamCompletedEvent event) {
        log.info("Recorded exam completion activity for userId={}, sessionId={}",
                event.getUserId(), event.getSessionId());
    }
}
