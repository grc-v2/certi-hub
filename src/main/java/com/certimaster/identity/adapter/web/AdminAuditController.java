package com.certimaster.identity.adapter.web;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.identity.adapter.web.response.AuditLogResponse;
import com.certimaster.identity.application.query.AdminAuditService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/admin/audit")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuditController {

    private final AdminAuditService adminAuditService;

    public AdminAuditController(AdminAuditService adminAuditService) {
        this.adminAuditService = adminAuditService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<AuditLogResponse>> list(
            @PageableDefault(size = 20, sort = "occurredAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Long actorId,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        Page<AuditLogResponse> page = adminAuditService.search(actorId, resourceType, from, to, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLogResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(adminAuditService.getById(id));
    }
}
