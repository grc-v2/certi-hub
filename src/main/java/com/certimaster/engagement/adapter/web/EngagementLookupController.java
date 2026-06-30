package com.certimaster.engagement.adapter.web;

import com.certimaster.engagement.application.lookup.EngagementLookupService;
import com.certimaster.shared.dto.LookupOption;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/engagement/lookups")
public class EngagementLookupController {

    private final EngagementLookupService engagementLookupService;

    public EngagementLookupController(EngagementLookupService engagementLookupService) {
        this.engagementLookupService = engagementLookupService;
    }

    @GetMapping("/posts")
    public ResponseEntity<Map<String, List<LookupOption>>> getPostLookups() {
        Map<String, List<LookupOption>> constants = engagementLookupService.constants();
        Map<String, List<LookupOption>> response = new LinkedHashMap<>();
        response.put("statuses", constants.get("postStatuses"));
        // Additional dropdowns if needed, like categories
        response.put("contentTypes", constants.get("postContentTypes"));
        response.put("difficulties", constants.get("postDifficultyLevels"));
        response.put("tagCategories", constants.get("postTagCategories"));
        return ResponseEntity.ok(response);
    }
}
