package com.certimaster.identity.application.query;

import com.certimaster.shared.util.InputSanitizer;
import com.certimaster.shared.exception.ResourceNotFoundException;
import com.certimaster.identity.application.command.SessionService;
import com.certimaster.identity.adapter.persistence.UserReadRepository;
import com.certimaster.identity.adapter.persistence.projection.UserSummaryProjection;
import com.certimaster.identity.adapter.web.mapper.UserMapper;
import com.certimaster.identity.adapter.web.response.UserDetailResponse;
import com.certimaster.identity.adapter.web.response.UserSummaryResponse;
import com.certimaster.identity.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class AdminUserQueryService {

    private final UserReadRepository userRepository;
    private final SessionService sessionService;
    private final UserMapper userMapper;
    private final InputSanitizer inputSanitizer;

    public AdminUserQueryService(UserReadRepository userRepository,
                                 SessionService sessionService,
                                 UserMapper userMapper,
                                 InputSanitizer inputSanitizer) {
        this.userRepository = userRepository;
        this.sessionService = sessionService;
        this.userMapper = userMapper;
        this.inputSanitizer = inputSanitizer;
    }

    public Page<UserSummaryResponse> listUsers(ListUsersQuery query) {
        if (query.search() != null && !query.search().isBlank()) {
            String sanitized = inputSanitizer.sanitizeSearchQuery(query.search());
            return userRepository.searchSummary(sanitized, query.pageable())
                    .map(this::toUserSummaryResponse);
        }
        return userRepository.findSummaryByDeletedAtIsNull(query.pageable())
                .map(this::toUserSummaryResponse);
    }

    public UserDetailResponse getUserById(GetAdminUserByIdQuery query) {
        return userMapper.toUserDetailDTO(findActiveUser(query.id()));
    }

    public List<Map<String, Object>> getActiveSessions(GetUserActiveSessionsQuery query) {
        findActiveUser(query.userId());
        return sessionService.getActiveSessions(query.userId());
    }

    private User findActiveUser(Long id) {
        return userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> ResourceNotFoundException.byId("User", id));
    }

    private UserSummaryResponse toUserSummaryResponse(UserSummaryProjection projection) {
        UserSummaryResponse response = new UserSummaryResponse();
        response.setId(projection.getId());
        response.setEmail(projection.getEmail());
        response.setFullName(projection.getFullName());
        response.setUsername(projection.getUsername());
        response.setStatus(projection.getStatus());
        response.setRoleCodes(parseRoleCodes(projection.getRoleCodes()));
        response.setRegistrationDate(projection.getRegistrationDate());
        response.setLastLoginDate(projection.getLastLoginDate());
        return response;
    }

    private Set<String> parseRoleCodes(String roleCodes) {
        if (roleCodes == null || roleCodes.isBlank()) {
            return Set.of();
        }
        return Stream.of(roleCodes.split(","))
                .map(String::trim)
                .filter(role -> !role.isBlank())
                .collect(Collectors.toSet());
    }
}
