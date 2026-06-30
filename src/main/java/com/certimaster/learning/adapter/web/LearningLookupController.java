package com.certimaster.learning.adapter.web;

import com.certimaster.learning.application.lookup.LearningLookupService;
import com.certimaster.shared.dto.LookupOption;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/learning/lookups")
public class LearningLookupController {

    private final LearningLookupService learningLookupService;

    public LearningLookupController(LearningLookupService learningLookupService) {
        this.learningLookupService = learningLookupService;
    }

    @GetMapping("/questions")
    public ResponseEntity<Map<String, List<LookupOption>>> getQuestionLookups() {
        Map<String, List<LookupOption>> constants = learningLookupService.constants();
        Map<String, List<LookupOption>> response = new LinkedHashMap<>();
        response.put("types", constants.get("questionTypes"));
        response.put("difficulties", constants.get("questionDifficulties"));
        response.put("statuses", constants.get("questionStatuses"));
        return ResponseEntity.ok(response);
    }
}
