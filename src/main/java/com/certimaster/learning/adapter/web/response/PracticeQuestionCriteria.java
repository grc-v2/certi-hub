package com.certimaster.learning.adapter.web.response;

import java.util.List;

public record PracticeQuestionCriteria(Long certificationId, Long questionBankId, List<Long> domainIds, List<String> difficulties, int questionCount, Long userId) {

}
