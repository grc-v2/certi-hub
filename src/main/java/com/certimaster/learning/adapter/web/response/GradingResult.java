package com.certimaster.learning.adapter.web.response;

import java.util.List;

public record GradingResult(boolean correct, List<Long> correctOptionIds) {

}
