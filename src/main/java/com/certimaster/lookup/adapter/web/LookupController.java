package com.certimaster.lookup.adapter.web;

import com.certimaster.engagement.application.lookup.EngagementLookupService;
import com.certimaster.learning.application.lookup.LearningLookupService;
import com.certimaster.shared.dto.LookupOption;
import com.certimaster.shared.dto.LookupResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/lookups")
public class LookupController {

    private final LearningLookupService learningLookupService;
    private final EngagementLookupService engagementLookupService;

    public LookupController(LearningLookupService learningLookupService,
                            EngagementLookupService engagementLookupService) {
        this.learningLookupService = learningLookupService;
        this.engagementLookupService = engagementLookupService;
    }

    @GetMapping
    public ResponseEntity<LookupResponse> getLookups(
            @RequestParam(defaultValue = "false") boolean includeInactive,
            @RequestParam(required = false) Long certificationId) {
        Map<String, List<LookupOption>> constants = new LinkedHashMap<>();
        constants.putAll(learningLookupService.constants());
        constants.putAll(engagementLookupService.constants());

        Map<String, List<LookupOption>> catalogs = new LinkedHashMap<>();
        catalogs.putAll(learningLookupService.catalogs(includeInactive, certificationId));
        catalogs.putAll(engagementLookupService.catalogs());

        return ResponseEntity.ok(new LookupResponse(constants, catalogs));
    }
}
