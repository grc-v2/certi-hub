package com.certimaster.identity.adapter.web;

import com.certimaster.shared.dto.PageResponse;
import com.certimaster.identity.adapter.web.request.BulkUserRequest;
import com.certimaster.identity.adapter.web.request.UpdateUserRequest;
import com.certimaster.identity.adapter.web.response.BulkOperationResult;
import com.certimaster.identity.adapter.web.response.UserDetailResponse;
import com.certimaster.identity.adapter.web.response.UserSummaryResponse;

import com.certimaster.identity.application.command.AdminUserService;
import com.certimaster.identity.application.command.BulkChangeUserStatusCommand;
import com.certimaster.identity.application.command.ChangeAdminUserStatusCommand;
import com.certimaster.identity.application.command.DeleteAdminUserCommand;
import com.certimaster.identity.application.command.ForceLogoutAllUserSessionsCommand;
import com.certimaster.identity.application.command.ForceLogoutUserSessionCommand;
import com.certimaster.identity.application.command.UpdateAdminUserCommand;
import com.certimaster.identity.application.query.AdminUserQueryService;
import com.certimaster.identity.application.query.GetAdminUserByIdQuery;
import com.certimaster.identity.application.query.GetUserActiveSessionsQuery;
import com.certimaster.identity.application.query.ListUsersQuery;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private static final Logger log = LoggerFactory.getLogger(AdminUserController.class);

    private final AdminUserService adminUserService;
    private final AdminUserQueryService adminUserQueryService;

    public AdminUserController(AdminUserService adminUserService, AdminUserQueryService adminUserQueryService) {
        this.adminUserService = adminUserService;
        this.adminUserQueryService = adminUserQueryService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<UserSummaryResponse>> listUsers(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate registrationDateFrom,
            @RequestParam(required = false) LocalDate registrationDateTo) {

        log.info("GET /api/admin/users - page={}, size={}, search={}", pageable.getPageNumber(), pageable.getPageSize(), search);
        Page<UserSummaryResponse> users = adminUserQueryService.listUsers(new ListUsersQuery(
                pageable,
                search,
                roleCode,
                status,
                registrationDateFrom,
                registrationDateTo
        ));
        return ResponseEntity.ok(PageResponse.of(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailResponse> getUserById(@PathVariable Long id) {
        log.info("GET /api/admin/users/{}", id);
        return ResponseEntity.ok(adminUserQueryService.getUserById(new GetAdminUserByIdQuery(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDetailResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        log.info("PUT /api/admin/users/{}", id);
        return ResponseEntity.ok(adminUserService.updateUser(toUpdateCommand(id, request)));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        log.info("PATCH /api/admin/users/{}/activate", id);
        adminUserService.activateUser(new ChangeAdminUserStatusCommand(id, "ACTIVE"));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        log.info("PATCH /api/admin/users/{}/deactivate", id);
        adminUserService.deactivateUser(new ChangeAdminUserStatusCommand(id, "INACTIVE"));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/admin/users/{}", id);
        adminUserService.deleteUser(new DeleteAdminUserCommand(id));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk/activate")
    public ResponseEntity<BulkOperationResult> bulkActivate(@Valid @RequestBody BulkUserRequest request) {
        log.info("POST /api/admin/users/bulk/activate - count={}", request.getUserIds().size());
        return ResponseEntity.ok(adminUserService.bulkActivate(new BulkChangeUserStatusCommand(request.getUserIds(), "ACTIVE")));
    }

    @PostMapping("/bulk/deactivate")
    public ResponseEntity<BulkOperationResult> bulkDeactivate(@Valid @RequestBody BulkUserRequest request) {
        log.info("POST /api/admin/users/bulk/deactivate - count={}", request.getUserIds().size());
        return ResponseEntity.ok(adminUserService.bulkDeactivate(new BulkChangeUserStatusCommand(request.getUserIds(), "INACTIVE")));
    }

    @GetMapping("/{id}/sessions/active")
    public ResponseEntity<List<Map<String, Object>>> getActiveSessions(@PathVariable Long id) {
        log.info("GET /api/admin/users/{}/sessions/active", id);
        return ResponseEntity.ok(adminUserQueryService.getActiveSessions(new GetUserActiveSessionsQuery(id)));
    }

    @DeleteMapping("/{id}/sessions/{sessionId}")
    public ResponseEntity<Void> forceLogoutSession(@PathVariable Long id, @PathVariable String sessionId) {
        log.info("DELETE /api/admin/users/{}/sessions/{}", id, sessionId);
        adminUserService.forceLogoutSession(new ForceLogoutUserSessionCommand(id, sessionId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/sessions")
    public ResponseEntity<Map<String, Integer>> forceLogoutAllSessions(@PathVariable Long id) {
        log.info("DELETE /api/admin/users/{}/sessions", id);
        int count = adminUserService.forceLogoutAllSessions(new ForceLogoutAllUserSessionsCommand(id));
        return ResponseEntity.ok(Map.of("count", count));
    }

    private UpdateAdminUserCommand toUpdateCommand(Long id, UpdateUserRequest request) {
        return new UpdateAdminUserCommand(
                id,
                request.getEmail(),
                request.getFullName(),
                request.getPhone(),
                request.getRoleCode(),
                request.getStatus()
        );
    }
}
