package com.certimaster.learning.domain.service;

import com.certimaster.learning.adapter.web.response.DomainScore;
import com.certimaster.learning.adapter.web.response.GradingResult;
import com.certimaster.learning.adapter.web.response.SessionScoreResult;
import com.certimaster.learning.domain.entity.CertificationDomain;
import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.domain.entity.Question;
import com.certimaster.learning.domain.entity.QuestionOption;
import com.certimaster.learning.domain.entity.UserAnswer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GradingService {

    public GradingResult gradeAnswer(Question question, List<Long> selectedOptionIds) {
        Set<Long> correctOptionIdSet = question.getQuestionOptions().stream()
                .filter(QuestionOption::getIsCorrect)
                .map(QuestionOption::getId)
                .collect(Collectors.toSet());

        boolean isCorrect = correctOptionIdSet.size() == selectedOptionIds.size()
                && correctOptionIdSet.containsAll(selectedOptionIds);

        return new GradingResult(isCorrect, List.copyOf(correctOptionIdSet));
    }

    public SessionScoreResult calculateSessionScore(UserExamSession session, List<UserAnswer> answers) {
        int totalQuestions = session.getTotalQuestions();
        int correctCount = 0;

        Map<Long, int[]> domainStats = new HashMap<>();
        Map<Long, String> domainNames = new HashMap<>();

        for (UserAnswer answer : answers) {
            if (Boolean.TRUE.equals(answer.getIsCorrect())) {
                correctCount++;
            }

            Question question = answer.getQuestion();
            CertificationDomain domain = question.getDomain();
            if (domain != null) {
                Long domainId = domain.getId();
                domainStats.computeIfAbsent(domainId, k -> new int[2]);
                domainStats.get(domainId)[0]++; // total
                if (Boolean.TRUE.equals(answer.getIsCorrect())) {
                    domainStats.get(domainId)[1]++; // correct
                }
                domainNames.putIfAbsent(domainId, domain.getName());
            }
        }

        BigDecimal score = BigDecimal.valueOf(correctCount);
        BigDecimal scorePercentage = totalQuestions > 0
                ? BigDecimal.valueOf(correctCount * 100.0 / totalQuestions).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        boolean passed = false;
        if (session.getExam() != null && session.getExam().getPassingScore() != null) {
            passed = scorePercentage.compareTo(BigDecimal.valueOf(session.getExam().getPassingScore())) >= 0;
        }

        List<DomainScore> domainScores = new ArrayList<>();
        for (Map.Entry<Long, int[]> entry : domainStats.entrySet()) {
            int total = entry.getValue()[0];
            int correct = entry.getValue()[1];
            BigDecimal pct = total > 0
                    ? BigDecimal.valueOf(correct * 100.0 / total).setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            domainScores.add(new DomainScore(
                    entry.getKey(),
                    domainNames.get(entry.getKey()),
                    total, correct, pct));
        }

        return new SessionScoreResult(score, scorePercentage, passed, domainScores);
    }
}
