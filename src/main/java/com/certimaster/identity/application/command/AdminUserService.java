package com.certimaster.identity.application.command;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.identity.adapter.web.response.BulkOperationError;
import com.certimaster.identity.adapter.web.response.BulkOperationResult;
import com.certimaster.identity.adapter.web.response.UserDetailResponse;
import com.certimaster.identity.domain.entity.Role;
import com.certimaster.identity.domain.entity.User;
import com.certimaster.identity.adapter.web.mapper.UserMapper;
import com.certimaster.identity.adapter.persistence.RoleRepository;
import com.certimaster.identity.adapter.persistence.UserWriteRepository;
import com.certimaster.shared.util.InputSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class AdminUserService {

    private static final Logger log = LoggerFactory.getLogger(AdminUserService.class);

    @Value("${auth.admin.bulk.max-size:500}")
    private int bulkOperationMaxSize;

    private final UserWriteRepository userRepository;
    private final RoleRepository roleRepository;
    private final SessionService sessionService;
    private final ApplicationEventPublisher applicationEvents;
    private final UserMapper userMapper;
    private final InputSanitizer inputSanitizer;

    public AdminUserService(UserWriteRepository userRepository,
                            RoleRepository roleRepository,
                            SessionService sessionService,
                            ApplicationEventPublisher applicationEvents,
                            UserMapper userMapper,
                            InputSanitizer inputSanitizer) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.sessionService = sessionService;
        this.applicationEvents = applicationEvents;
        this.userMapper = userMapper;
        this.inputSanitizer = inputSanitizer;
    }

    @Transactional
    public UserDetailResponse updateUser(UpdateAdminUserCommand command) {
        User user = findActiveUser(command.id());
        Map<String, Object> changes = new HashMap<>();

        if (command.email() != null && !command.email().equals(user.getEmail())) {
            String email = inputSanitizer.sanitizeEmail(command.email());
            if (userRepository.existsByEmail(email)) {
                throw BusinessException.duplicateResource("User", "email: " + email);
            }
            changes.put("email", Map.of("old", user.getEmail(), "new", email));
            user.setEmail(email);
        }
        if (command.fullName() != null && !command.fullName().equals(user.getFullName())) {
            String name = inputSanitizer.sanitize(command.fullName());
            changes.put("fullName", Map.of("old", user.getFullName(), "new", name));
            user.setFullName(name);
        }
        if (command.phone() != null && !command.phone().equals(user.getPhone())) {
            String phone = inputSanitizer.sanitizePhone(command.phone());
            changes.put("phone", Map.of("old", user.getPhone(), "new", phone));
            user.setPhone(phone);
        }
        if (command.roleCode() != null) {
            Role role = roleRepository.findByCode(command.roleCode())
                .orElseThrow(() -> ResourceNotFoundException.byField("Role", "code", command.roleCode()));
            Set<Role> newRoles = new HashSet<>();
            newRoles.add(role);
            changes.put("roles", Map.of("old", user.getRoles(), "new", newRoles));
            user.setRoles(newRoles);
        }
        if (command.status() != null && !command.status().equals(user.getStatus())) {
            changes.put("status", Map.of("old", user.getStatus(), "new", command.status()));
            user.setStatus(command.status());
        }

        User saved = userRepository.save(user);
        publishUserEvent("user_updated", saved.getId(), null,
            Map.of("userId", saved.getId(), "changedFields", changes));

        return userMapper.toUserDetailDTO(saved);
    }

    @Transactional
    public void activateUser(ChangeAdminUserStatusCommand command) {
        User user = findActiveUser(command.id());
        String oldStatus = user.getStatus();
        user.setStatus(command.status());
        userRepository.save(user);

        publishUserEvent("account_status_changed", command.id(), null,
            Map.of("userId", command.id(), "oldStatus", oldStatus, "newStatus", command.status()));
    }

    @Transactional
    public void deactivateUser(ChangeAdminUserStatusCommand command) {
        User user = findActiveUser(command.id());
        String oldStatus = user.getStatus();
        user.setStatus(command.status());
        userRepository.save(user);

        int terminated = sessionService.forceLogoutAllSessions(command.id(), "DEACTIVATED");
        publishUserEvent("account_status_changed", command.id(), null,
            Map.of("userId", command.id(), "oldStatus", oldStatus, "newStatus", command.status(), "terminatedSessions", terminated));
    }

    @Transactional
    public void deleteUser(DeleteAdminUserCommand command) {
        User user = findActiveUser(command.id());
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        int terminated = sessionService.forceLogoutAllSessions(command.id(), "USER_DELETED");
        publishUserEvent("user_deleted", command.id(), null,
            Map.of("userId", command.id(), "deletedAt", user.getDeletedAt().toString(), "terminatedSessions", terminated));
    }

    @Transactional
    public BulkOperationResult bulkActivate(BulkChangeUserStatusCommand command) {
        validateBulkSize(command.userIds());
        return executeBulk(command.userIds(), userId -> activateUser(new ChangeAdminUserStatusCommand(userId, command.status())));
    }

    @Transactional
    public BulkOperationResult bulkDeactivate(BulkChangeUserStatusCommand command) {
        validateBulkSize(command.userIds());
        return executeBulk(command.userIds(), userId -> deactivateUser(new ChangeAdminUserStatusCommand(userId, command.status())));
    }

    @Transactional
    public void forceLogoutSession(ForceLogoutUserSessionCommand command) {
        findActiveUser(command.userId());
        sessionService.deleteSession(command.userId(), command.sessionId());
        publishSessionEvent("session_terminated", command.userId(), null,
            Map.of("sessionId", command.sessionId(), "userId", command.userId()));
    }

    @Transactional
    public int forceLogoutAllSessions(ForceLogoutAllUserSessionsCommand command) {
        findActiveUser(command.userId());
        int count = sessionService.forceLogoutAllSessions(command.userId(), "FORCE_LOGOUT_ALL");
        publishSessionEvent("sessions_terminated", command.userId(), null,
            Map.of("userId", command.userId(), "terminatedCount", count));
        return count;
    }

    private void publishUserEvent(String eventType, Long userId, Long adminId, Map<String, Object> data) {
        applicationEvents.publishEvent(new AdminUserEvent(eventType, userId, adminId, data, Instant.now()));
    }

    private void publishSessionEvent(String eventType, Long userId, Long adminId, Map<String, Object> data) {
        applicationEvents.publishEvent(new AdminSessionEvent(eventType, userId, adminId, data, Instant.now()));
    }

    private User findActiveUser(Long id) {
        return userRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> ResourceNotFoundException.byId("User", id));
    }

    private void validateBulkSize(List<Long> userIds) {
        if (userIds.size() > bulkOperationMaxSize) {
            throw BusinessException.invalidInput(
                "Bulk operation limit exceeded. Requested: " + userIds.size() + ", Maximum: " + bulkOperationMaxSize);
        }
    }

    private BulkOperationResult executeBulk(List<Long> userIds, java.util.function.Consumer<Long> action) {
        int success = 0, failed = 0;
        List<BulkOperationError> errors = new ArrayList<>();
        for (Long userId : userIds) {
            try {
                action.accept(userId);
                success++;
            } catch (Exception e) {
                failed++;
                errors.add(new BulkOperationError(userId, e.getMessage()));
                log.warn("Bulk operation failed for userId={}: {}", userId, e.getMessage());
            }
        }
        return new BulkOperationResult(success, failed, errors);
    }

    private record AdminUserEvent(
            String eventType,
            Long userId,
            Long adminId,
            Map<String, Object> data,
            Instant occurredAt
    ) {
    }

    private record AdminSessionEvent(
            String eventType,
            Long userId,
            Long adminId,
            Map<String, Object> data,
            Instant occurredAt
    ) {
    }
}
