package com.certimaster.learning.application.command;

import com.certimaster.learning.application.command.AbandonSessionCommand;
import com.certimaster.learning.application.command.CompleteSessionCommand;
import com.certimaster.learning.application.command.StartExamCommand;
import com.certimaster.learning.application.command.StartPracticeSessionCommand;
import com.certimaster.learning.application.command.SubmitAnswerCommand;
import com.certimaster.learning.application.command.TimeoutSessionCommand;
import com.certimaster.learning.adapter.web.response.AnswerFeedbackResponse;
import com.certimaster.learning.adapter.web.response.ExamResultResponse;
import com.certimaster.learning.adapter.web.response.ExamSessionResponse;
import org.springframework.stereotype.Service;

@Service
public class ExamSessionCommandService {

    private final StartExamHandler startExamHandler;
    private final StartPracticeSessionHandler startPracticeSessionHandler;
    private final SubmitAnswerHandler submitAnswerHandler;
    private final CompleteSessionHandler completeSessionHandler;
    private final TimeoutSessionHandler timeoutSessionHandler;
    private final AbandonSessionHandler abandonSessionHandler;
    private final PauseSessionHandler pauseSessionHandler;
    private final ResumePlayHandler resumePlayHandler;

    public ExamSessionCommandService(StartExamHandler startExamHandler,
                                     StartPracticeSessionHandler startPracticeSessionHandler,
                                     SubmitAnswerHandler submitAnswerHandler,
                                     CompleteSessionHandler completeSessionHandler,
                                     TimeoutSessionHandler timeoutSessionHandler,
                                     AbandonSessionHandler abandonSessionHandler,
                                     PauseSessionHandler pauseSessionHandler,
                                     ResumePlayHandler resumePlayHandler) {
        this.startExamHandler = startExamHandler;
        this.startPracticeSessionHandler = startPracticeSessionHandler;
        this.submitAnswerHandler = submitAnswerHandler;
        this.completeSessionHandler = completeSessionHandler;
        this.timeoutSessionHandler = timeoutSessionHandler;
        this.abandonSessionHandler = abandonSessionHandler;
        this.pauseSessionHandler = pauseSessionHandler;
        this.resumePlayHandler = resumePlayHandler;
    }

    public ExamSessionResponse startExam(StartExamCommand command) {
        return startExamHandler.handle(command);
    }

    public ExamSessionResponse startPracticeSession(StartPracticeSessionCommand command) {
        return startPracticeSessionHandler.handle(command);
    }

    public AnswerFeedbackResponse submitAnswer(SubmitAnswerCommand command) {
        return submitAnswerHandler.handle(command);
    }

    public ExamResultResponse completeSession(CompleteSessionCommand command) {
        return completeSessionHandler.handle(command);
    }

    public ExamResultResponse timeoutSession(TimeoutSessionCommand command) {
        return timeoutSessionHandler.handle(command);
    }

    public void abandonSession(AbandonSessionCommand command) {
        abandonSessionHandler.handle(command);
    }

    public void pauseSession(PauseSessionCommand command) {
        pauseSessionHandler.handle(command);
    }

    public void resumePlay(ResumePlayCommand command) {
        resumePlayHandler.handle(command);
    }
}
