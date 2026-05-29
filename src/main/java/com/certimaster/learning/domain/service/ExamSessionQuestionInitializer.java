package com.certimaster.learning.domain.service;

import com.certimaster.learning.domain.entity.UserAnswer;
import com.certimaster.learning.domain.entity.UserExamSession;
import com.certimaster.learning.adapter.persistence.UserAnswerRepository;
import com.certimaster.learning.adapter.web.response.QuestionResponse;
import com.certimaster.learning.domain.entity.Question;
import com.certimaster.learning.domain.entity.QuestionOption;
import com.certimaster.learning.adapter.web.mapper.QuestionMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExamSessionQuestionInitializer {

    private final UserAnswerRepository userAnswerRepository;
    private final QuestionMapper questionMapper;

    public ExamSessionQuestionInitializer(UserAnswerRepository userAnswerRepository,
                                          QuestionMapper questionMapper) {
        this.userAnswerRepository = userAnswerRepository;
        this.questionMapper = questionMapper;
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

    public List<QuestionResponse> buildOrderedQuestionResponses(List<Question> questions, List<UserAnswer> savedAnswers) {
        Map<Long, Map<Long, Integer>> orderByQuestion = new HashMap<>();
        for (UserAnswer userAnswer : savedAnswers) {
            if (userAnswer.getOptionOrderMap() != null) {
                orderByQuestion.put(userAnswer.getQuestion().getId(), userAnswer.getOptionOrderMap());
            }
        }
        return questions.stream().map(question -> {
            QuestionResponse response = questionMapper.toResponseWithoutCorrect(question);
            Map<Long, Integer> orderMap = orderByQuestion.get(question.getId());
            if (orderMap != null && !orderMap.isEmpty()) {
                response.setOptions(response.getOptions().stream()
                        .sorted(Comparator.comparingInt(option -> orderMap.getOrDefault(option.getId(), Integer.MAX_VALUE)))
                        .toList());
            }
            return response;
        }).toList();
    }
}
