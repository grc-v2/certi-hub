package com.certimaster.identity.application.query;

import com.certimaster.shared.exception.UnauthorizedException;
import com.certimaster.identity.domain.entity.Role;
import com.certimaster.identity.adapter.persistence.UserReadRepository;
import com.certimaster.identity.adapter.web.response.UserResponse;
import com.certimaster.identity.domain.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AuthQueryService {

    private final UserReadRepository userRepository;
    private final PermissionService permissionService;

    public AuthQueryService(UserReadRepository userRepository, PermissionService permissionService) {
        this.userRepository = userRepository;
        this.permissionService = permissionService;
    }

    public UserResponse getCurrentUser(GetCurrentUserQuery query) {
        User user = userRepository.findByIdWithRoles(query.userId())
                .orElseThrow(UnauthorizedException::invalidCredentials);

        Set<String> permissions = permissionService.getUserPermissions(query.userId());
        return buildUserResponse(user, permissions);
    }

    private UserResponse buildUserResponse(User user, Set<String> permissions) {
        UserResponse.UserInfo userInfo = UserResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .username(user.getUsername())
                .status(user.getStatus())
                .phone(user.getPhone())
                .emailVerified(user.getEmailVerified())
                .roles(user.getRoles().stream()
                        .map(role -> UserResponse.RoleInfo.builder()
                                .id(role.getId())
                                .code(role.getCode())
                                .name(role.getName())
                                .isPrimary(true)
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return UserResponse.builder()
                .user(userInfo)
                .permissions(permissions)
                .build();
    }
}
