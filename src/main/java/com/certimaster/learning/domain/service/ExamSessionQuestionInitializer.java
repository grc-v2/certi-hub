package com.certimaster.learning.domain.service;

import com.certimaster.learning.domain.entity.UserAnswer;
import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.adapter.persistence.UserAnswerRepository;
import com.certimaster.learning.domain.entity.Question;
import com.certimaster.learning.domain.entity.QuestionOption;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExamSessionQuestionInitializer {

    private final UserAnswerRepository userAnswerRepository;

    public ExamSessionQuestionInitializer(UserAnswerRepository userAnswerRepository) {
        this.userAnswerRepository = userAnswerRepository;
    }

    public List<UserAnswer> createUserAnswers(UserExamSession session, List<Question> questions) {
        List<UserAnswer> userAnswers = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            Long[] correctOptionIds = question.getQuestionOptions().stream()
                    .filter(QuestionOption::getIsCorrect)
                    .map(QuestionOption::getId)
                    .toArray(Long[]::new);

            List<QuestionOption> options = new ArrayList<>(question.getQuestionOptions());
            Collections.shuffle(options);
            Map<Long, Integer> optionOrderMap = new HashMap<>();
            for (int j = 0; j < options.size(); j++) {
                optionOrderMap.put(options.get(j).getId(), j);
            }

            UserAnswer answer = UserAnswer.builder()
                    .userExamSession(session)
                    .question(question)
                    .correctOptionIds(correctOptionIds)
                    .isCorrect(null)
                    .isFlagged(false)
                    .timeSpentSeconds(0)
                    .questionOrder(i)
                    .optionOrderMap(optionOrderMap)
                    .build();
            userAnswers.add(answer);
        }
        return userAnswerRepository.saveAll(userAnswers);
    }
}
