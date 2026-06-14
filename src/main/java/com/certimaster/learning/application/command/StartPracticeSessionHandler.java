package com.certimaster.learning.application.command;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.learning.domain.entity.Certification;
import com.certimaster.learning.adapter.persistence.CertificationWriteRepository;
import com.certimaster.learning.application.command.StartPracticeSessionCommand;
import com.certimaster.learning.adapter.web.response.ExamSessionResponse;
import com.certimaster.learning.domain.model.PracticeQuestionCriteria;
import com.certimaster.learning.domain.entity.UserAnswer;
import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.adapter.persistence.UserExamSessionRepository;
import com.certimaster.learning.domain.service.ExamSessionQuestionInitializer;
import com.certimaster.learning.domain.service.QuestionSelectionService;
import com.certimaster.learning.adapter.web.mapper.OrderedQuestionResponseAssembler;
import com.certimaster.learning.adapter.web.response.QuestionResponse;
import com.certimaster.learning.domain.entity.Question;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.certimaster.learning.domain.service.ExamSessionGuard.MODE_PRACTICE;
import static com.certimaster.learning.domain.service.ExamSessionGuard.STATUS_IN_PROGRESS;

@Component
class StartPracticeSessionHandler {


    private final UserExamSessionRepository userExamSessionRepository;
    private final CertificationWriteRepository certificationRepository;
    private final QuestionSelectionService questionSelectionService;
    private final ExamSessionQuestionInitializer questionInitializer;
    private final OrderedQuestionResponseAssembler questionResponseAssembler;

    @Value("${exam.session.max-concurrent:3}")
    private int maxConcurrentSessions;

    StartPracticeSessionHandler(UserExamSessionRepository userExamSessionRepository,
                                CertificationWriteRepository certificationRepository,
                                QuestionSelectionService questionSelectionService,
                                ExamSessionQuestionInitializer questionInitializer,
                                OrderedQuestionResponseAssembler questionResponseAssembler) {
        this.userExamSessionRepository = userExamSessionRepository;
        this.certificationRepository = certificationRepository;
        this.questionSelectionService = questionSelectionService;
        this.questionInitializer = questionInitializer;
        this.questionResponseAssembler = questionResponseAssembler;
    }

    @Transactional
    ExamSessionResponse handle(StartPracticeSessionCommand command) {
        validateConcurrentSessions(command.userId());

        Certification certification = certificationRepository.findById(command.certificationId())
                .orElseThrow(() -> ResourceNotFoundException.byId("Certification", command.certificationId()));

        PracticeQuestionCriteria criteria = new PracticeQuestionCriteria(
                command.certificationId(),
                command.questionBankId(),
                command.domainIds(),
                command.difficulties(),
                command.questionCount(),
                command.userId()
        );

        List<Question> questions = questionSelectionService.selectPracticeQuestions(criteria);
        if (questions.isEmpty()) {
            throw BusinessException.invalidInput("No questions found matching the criteria");
        }

        LocalDateTime startTime = LocalDateTime.now();

        UserExamSession session = UserExamSession.builder()
                .userId(command.userId())
                .certification(certification)
                .startTime(startTime)
                .status(STATUS_IN_PROGRESS)
                .mode(MODE_PRACTICE)
                .examTitle("Practice: " + certification.getName())
                .totalQuestions(questions.size())
                .answeredCount(0)
                .correctCount(0)
                .wrongCount(0)
                .unansweredCount(questions.size())
                .flaggedCount(0)
                .timeSpentSeconds(0)
                .domainFilter(command.domainIds())
                .difficultyFilter(command.difficulties())
                .build();

        userExamSessionRepository.save(session);

        List<UserAnswer> savedAnswers = questionInitializer.createUserAnswers(session, questions);
        List<QuestionResponse> questionResponses = questionResponseAssembler.buildOrderedQuestionResponses(questions, savedAnswers);

        return ExamSessionResponse.builder()
                .id(session.getId())
                .certificationId(certification.getId())
                .certificationName(certification.getName())
                .mode(MODE_PRACTICE)
                .status(STATUS_IN_PROGRESS)
                .startTime(startTime)
                .totalQuestions(questions.size())
                .currentQuestionIndex(0)
                .questions(questionResponses)
                .build();
    }

    private void validateConcurrentSessions(Long userId) {
        int activeCount = userExamSessionRepository.countByUserIdAndStatus(userId, STATUS_IN_PROGRESS);
        if (activeCount >= maxConcurrentSessions) {
            throw BusinessException.invalidInput(
                    String.format("Maximum %d concurrent sessions allowed. Please complete or abandon existing sessions first.",
                            maxConcurrentSessions));
        }
    }
}
