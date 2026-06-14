package com.certimaster.learning.adapter.web.mapper;

import com.certimaster.learning.adapter.web.response.QuestionResponse;
import com.certimaster.learning.domain.entity.Question;
import com.certimaster.learning.domain.entity.UserAnswer;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps selected questions to client-facing {@link QuestionResponse}s with correct answers stripped,
 * ordering each question's options to match the per-session shuffle recorded on its {@code UserAnswer}.
 */
@Component
public class OrderedQuestionResponseAssembler {

    private final QuestionMapper questionMapper;

    public OrderedQuestionResponseAssembler(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
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
