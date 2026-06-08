package com.certimaster.learning.adapter.web;

import com.certimaster.shared.security.SecurityUtils;
import com.certimaster.learning.application.command.AbandonSessionCommand;
import com.certimaster.learning.application.command.CompleteSessionCommand;
import com.certimaster.learning.application.command.PauseSessionCommand;
import com.certimaster.learning.application.command.ResumePlayCommand;
import com.certimaster.learning.application.command.StartExamCommand;
import com.certimaster.learning.application.command.StartPracticeSessionCommand;
import com.certimaster.learning.application.command.SubmitAnswerCommand;
import com.certimaster.learning.application.command.TimeoutSessionCommand;
import com.certimaster.learning.adapter.web.request.AnswerQuestionRequest;
import com.certimaster.learning.adapter.web.request.PauseSessionRequest;
import com.certimaster.learning.adapter.web.request.StartExamRequest;
import com.certimaster.learning.adapter.web.request.StartPracticeRequest;
import com.certimaster.learning.adapter.web.response.AnswerFeedbackResponse;
import com.certimaster.learning.adapter.web.response.ExamResultResponse;
import com.certimaster.learning.adapter.web.response.ExamSessionResponse;
import com.certimaster.learning.adapter.web.response.ResumeSessionResponse;
import com.certimaster.learning.adapter.web.response.ReviewSessionResponse;
import com.certimaster.learning.adapter.web.response.UserExamSessionResponse;
import com.certimaster.learning.application.command.ExamSessionCommandService;
import com.certimaster.learning.application.query.ExamSessionQueryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exam-sessions")
public class ExamSessionController {

    private final ExamSessionCommandService commandService;
    private final ExamSessionQueryService queryService;

    public ExamSessionController(ExamSessionCommandService commandService,
                                 ExamSessionQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping("/exam/{examId}/start")
    public ResponseEntity<ExamSessionResponse> startExam(@PathVariable Long examId,
                                                          @RequestBody(required = false) StartExamRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        String username = SecurityUtils.getCurrentUsername();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commandService.startExam(new StartExamCommand(
                        examId,
                        userId,
                        username
                )));
    }

    @PostMapping("/practice/start")
    public ResponseEntity<ExamSessionResponse> startPracticeSession(
            @Valid @RequestBody StartPracticeRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commandService.startPracticeSession(new StartPracticeSessionCommand(
                        userId,
                        request.getCertificationId(),
                        request.getQuestionBankId(),
                        request.getDomainIds(),
                        request.getDifficulties(),
                        request.getQuestionCount()
                )));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<UserExamSessionResponse> getSession(@PathVariable Long sessionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(queryService.getSession(sessionId, userId));
    }

    @GetMapping("/{sessionId}/resume")
    public ResponseEntity<ResumeSessionResponse> resumeSession(@PathVariable Long sessionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(queryService.resumeSession(sessionId, userId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserExamSessionResponse>> getActiveSessions() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(queryService.getActiveSessions(userId));
    }

    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<AnswerFeedbackResponse> submitAnswer(@PathVariable Long sessionId,
                                                                @Valid @RequestBody AnswerQuestionRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(commandService.submitAnswer(new SubmitAnswerCommand(
                sessionId,
                userId,
                request.getQuestionId(),
                request.getSelectedOptionIds(),
                request.getTimeSpentSeconds(),
                request.getIsFlagged()
        )));
    }

    @PostMapping("/{sessionId}/complete")
    public ResponseEntity<ExamResultResponse> completeSession(@PathVariable Long sessionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(commandService.completeSession(new CompleteSessionCommand(sessionId, userId)));
    }

    @PostMapping("/{sessionId}/timeout")
    public ResponseEntity<ExamResultResponse> timeoutSession(@PathVariable Long sessionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(commandService.timeoutSession(new TimeoutSessionCommand(sessionId, userId)));
    }

    @PostMapping("/{sessionId}/abandon")
    public ResponseEntity<Void> abandonSession(@PathVariable Long sessionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        commandService.abandonSession(new AbandonSessionCommand(sessionId, userId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{sessionId}/pause")
    public ResponseEntity<Void> pauseSession(@PathVariable Long sessionId,
                                             @RequestBody(required = false) PauseSessionRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Integer timeSpent = request != null ? request.getTimeSpentSeconds() : null;
        commandService.pauseSession(new PauseSessionCommand(sessionId, userId, timeSpent));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{sessionId}/resume-play")
    public ResponseEntity<Void> resumePlay(@PathVariable Long sessionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        commandService.resumePlay(new ResumePlayCommand(sessionId, userId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sessionId}/review")
    public ResponseEntity<ReviewSessionResponse> reviewSession(@PathVariable Long sessionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(queryService.reviewSession(sessionId, userId));
    }
}
