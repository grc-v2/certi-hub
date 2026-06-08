package com.certimaster.learning.domain.service;

import com.certimaster.learning.adapter.web.response.PracticeQuestionCriteria;
import com.certimaster.learning.adapter.web.response.QuestionSelectionCriteria;
import com.certimaster.learning.domain.entity.Exam;
import com.certimaster.learning.domain.entity.ExamQuestion;
import com.certimaster.learning.domain.entity.Question;
import com.certimaster.learning.domain.entity.QuestionBank;
import com.certimaster.learning.domain.entity.QuestionBankItem;
import com.certimaster.learning.adapter.persistence.QuestionBankItemRepository;
import com.certimaster.learning.adapter.persistence.QuestionReadRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionSelectionService  {


    private final QuestionReadRepository questionRepository;
    private final QuestionBankItemRepository questionBankItemRepository;

    public QuestionSelectionService(QuestionReadRepository questionRepository,
                                    QuestionBankItemRepository questionBankItemRepository) {
        this.questionRepository = questionRepository;
        this.questionBankItemRepository = questionBankItemRepository;
    }

    public List<Question> selectQuestionsForExam(Exam exam) {
        return selectQuestionsForExam(exam, false);
    }

    public List<Question> selectQuestionsForExam(Exam exam, boolean shuffle) {
        List<Question> ordered = exam.getExamQuestions().stream()
                .sorted(Comparator.comparing(ExamQuestion::getOrderIndex))
                .map(ExamQuestion::getQuestion)
                .collect(Collectors.toCollection(ArrayList::new));
        if (shuffle) {
            Collections.shuffle(ordered);
        }
        return ordered;
    }

    public List<Question> selectQuestionsFromBank(QuestionBank bank, QuestionSelectionCriteria criteria) {
        List<QuestionBankItem> items = questionBankItemRepository.findByQuestionBankIdOrderByPositionAsc(bank.getId());

        List<Question> candidates = items.stream()
                .map(QuestionBankItem::getQuestion)
                .filter(q -> "PUBLISHED".equals(q.getStatus()))
                .collect(Collectors.toList());

        // Apply domain filter
        if (criteria.domainIds() != null && !criteria.domainIds().isEmpty()) {
            candidates = candidates.stream()
                    .filter(q -> q.getDomain() != null && criteria.domainIds().contains(q.getDomain().getId()))
                    .collect(Collectors.toList());
        }

        // Apply difficulty filter
        if (criteria.difficulties() != null && !criteria.difficulties().isEmpty()) {
            candidates = candidates.stream()
                    .filter(q -> q.getDifficulty() != null && criteria.difficulties().contains(q.getDifficulty()))
                    .collect(Collectors.toList());
        }

        // Shuffle and limit
        List<Question> shuffled = new ArrayList<>(candidates);
        Collections.shuffle(shuffled);

        int count = Math.min(criteria.questionCount(), shuffled.size());
        return shuffled.subList(0, count);
    }

    public List<Question> selectPracticeQuestions(PracticeQuestionCriteria criteria) {
        List<Question> candidates;

        if (criteria.questionBankId() != null) {
            List<QuestionBankItem> items = questionBankItemRepository
                    .findByQuestionBankIdOrderByPositionAsc(criteria.questionBankId());
            candidates = items.stream()
                    .map(QuestionBankItem::getQuestion)
                    .filter(q -> "PUBLISHED".equals(q.getStatus()))
                    .collect(Collectors.toList());
        } else {
            candidates = new ArrayList<>(
                    questionRepository.findPublishedByCertificationId(criteria.certificationId()));
        }

        // Apply domain filter
        if (criteria.domainIds() != null && !criteria.domainIds().isEmpty()) {
            candidates = candidates.stream()
                    .filter(q -> q.getDomain() != null && criteria.domainIds().contains(q.getDomain().getId()))
                    .collect(Collectors.toList());
        }

        // Apply difficulty filter
        if (criteria.difficulties() != null && !criteria.difficulties().isEmpty()) {
            candidates = candidates.stream()
                    .filter(q -> q.getDifficulty() != null && criteria.difficulties().contains(q.getDifficulty()))
                    .collect(Collectors.toList());
        }

        // Shuffle and limit
        Collections.shuffle(candidates);
        int count = Math.min(criteria.questionCount(), candidates.size());
        return candidates.subList(0, count);
    }
}
