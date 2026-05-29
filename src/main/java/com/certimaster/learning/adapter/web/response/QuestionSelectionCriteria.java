package com.certimaster.learning.adapter.web.response;

import java.util.List;

public record QuestionSelectionCriteria(int questionCount, List<Long> domainIds, List<String> difficulties) {

}
