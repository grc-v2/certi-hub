package com.certimaster.learning.application.query;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.learning.domain.entity.CertificationDomain;
import com.certimaster.learning.adapter.web.response.QuestionOptionResponse;
import com.certimaster.learning.domain.entity.Exam;
import com.certimaster.learning.domain.entity.ExamQuestion;
import com.certimaster.learning.adapter.persistence.ExamQuestionRepository;
import com.certimaster.learning.adapter.persistence.ExamReadRepository;
import com.certimaster.learning.application.query.GetFlashcardQuestionsQuery;
import com.certimaster.learning.application.query.GetQuestionByIdQuery;
import com.certimaster.learning.application.query.GetQuestionForExamQuery;
import com.certimaster.learning.application.query.GetQuestionsByDomainQuery;
import com.certimaster.learning.application.query.GetQuestionsByExamQuery;
import com.certimaster.learning.application.query.SearchQuestionsQuery;
import com.certimaster.learning.adapter.web.response.ExamQuestionsGroupedResponse;
import com.certimaster.learning.adapter.web.response.PracticeQuestionCriteria;
import com.certimaster.learning.adapter.web.response.QuestionResponse;
import com.certimaster.learning.domain.entity.Question;
import com.certimaster.learning.adapter.web.mapper.QuestionMapper;
import com.certimaster.learning.adapter.persistence.QuestionReadRepository;
import com.certimaster.learning.domain.service.QuestionSelectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class QuestionQueryService {


    private static final String QUESTION = "Question";
    private static final String EXAM = "Exam";

    private final QuestionReadRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamReadRepository examRepository;
    private final QuestionSelectionService questionSelectionService;

    public QuestionQueryService(QuestionReadRepository questionRepository,
                                QuestionMapper questionMapper,
                                ExamQuestionRepository examQuestionRepository,
                                ExamReadRepository examRepository,
                                QuestionSelectionService questionSelectionService) {
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
        this.examQuestionRepository = examQuestionRepository;
        this.examRepository = examRepository;
        this.questionSelectionService = questionSelectionService;
    }

    public PageResponse<QuestionResponse> search(SearchQuestionsQuery query) {
        Sort sort = createSort(query.sortBy(), query.sortDirection());
        Pageable pageable = PageRequest.of(query.page(), query.size(), sort);

        Page<Question> page = questionRepository.search(
                query.keyword(),
                query.domainId(),
                query.certificationId(),
                query.type(),
                query.difficulty(),
                query.status(),
                pageable
        );

        Page<QuestionResponse> responsePage = page.map(questionMapper::toResponse);
        return PageResponse.of(responsePage);
    }

    public ExamQuestionsGroupedResponse getQuestionsByExamId(GetQuestionsByExamQuery query) {
        Exam exam = examRepository.findById(query.examId())
                .orElseThrow(() -> ResourceNotFoundException.byId(EXAM, query.examId()));

        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamIdWithDetails(query.examId());
        Map<Long, ExamQuestionsGroupedResponse.DomainQuestionGroup> domainGroupMap = new LinkedHashMap<>();

        for (ExamQuestion examQuestion : examQuestions) {
            Question question = examQuestion.getQuestion();
            CertificationDomain domain = question.getDomain();

            ExamQuestionsGroupedResponse.DomainQuestionGroup domainGroup = domainGroupMap.computeIfAbsent(
                    domain.getId(),
                    domainId -> ExamQuestionsGroupedResponse.DomainQuestionGroup.builder()
                            .domainId(domainId)
                            .domainName(domain.getName())
                            .domainCode(domain.getCode())
                            .questionCount(0)
                            .questions(new ArrayList<>())
                            .build()
            );

            List<QuestionOptionResponse> optionResponses = question.getQuestionOptions().stream()
                    .map(option -> QuestionOptionResponse.builder()
                            .id(option.getId())
                            .content(option.getContent())
                            .isCorrect(option.getIsCorrect())
                            .orderIndex(option.getOrderIndex())
                            .build())
                    .sorted(Comparator.comparingInt(o -> o.getOrderIndex() != null ? o.getOrderIndex() : 0))
                    .toList();

            ExamQuestionsGroupedResponse.QuestionInExam questionInExam =
                    ExamQuestionsGroupedResponse.QuestionInExam.builder()
                            .questionId(question.getId())
                            .orderIndex(examQuestion.getOrderIndex())
                            .type(question.getType())
                            .content(question.getContent())
                            .difficulty(question.getDifficulty())
                            .points(question.getPoints())
                            .timeLimitSeconds(question.getTimeLimitSeconds())
                            .options(optionResponses)
                            .build();

            domainGroup.getQuestions().add(questionInExam);
            domainGroup.setQuestionCount(domainGroup.getQuestionCount() + 1);
        }

        List<ExamQuestionsGroupedResponse.DomainQuestionGroup> domainGroups =
                new ArrayList<>(domainGroupMap.values());

        return ExamQuestionsGroupedResponse.builder()
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .totalQuestions(examQuestions.size())
                .domainGroups(domainGroups)
                .build();
    }

    public QuestionResponse getById(GetQuestionByIdQuery query) {
        Question question = findOrThrow(query.id());
        return questionMapper.toResponse(question);
    }

    public QuestionResponse getByIdForExam(GetQuestionForExamQuery query) {
        Question question = findOrThrow(query.id());
        return questionMapper.toResponseWithoutCorrect(question);
    }

    public List<QuestionResponse> getByDomainId(GetQuestionsByDomainQuery query) {
        return questionRepository.findByDomainId(query.domainId()).stream()
                .map(questionMapper::toResponse)
                .toList();
    }

    public List<QuestionResponse> getFlashcardQuestions(GetFlashcardQuestionsQuery query) {
        int count = query.count() != null && query.count() > 0 ? query.count() : 20;
        PracticeQuestionCriteria criteria = new PracticeQuestionCriteria(
                query.certificationId(),
                null,
                query.domainIds(),
                query.difficulties(),
                count,
                null
        );
        return questionSelectionService.selectPracticeQuestions(criteria).stream()
                .map(questionMapper::toResponse)
                .toList();
    }

    private Question findOrThrow(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.byId(QUESTION, id));
    }

    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, sortBy);
    }
}
