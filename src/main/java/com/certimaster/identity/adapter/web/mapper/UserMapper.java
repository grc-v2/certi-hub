package com.certimaster.identity.adapter.web.mapper;

import com.certimaster.identity.adapter.web.response.UserDetailResponse;
import com.certimaster.identity.adapter.web.response.UserSummaryResponse;
import com.certimaster.identity.domain.entity.Role;
import com.certimaster.identity.domain.entity.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserSummaryResponse toUserSummaryDTO(User user) {
        if (user == null) {
            return null;
        }

        UserSummaryResponse dto = new UserSummaryResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setUsername(user.getUsername());
        dto.setStatus(user.getStatus());
        dto.setRoleCodes(extractRoleCodes(user.getRoles()));
        dto.setRegistrationDate(user.getCreatedAt());
        dto.setLastLoginDate(user.getLastLoginDate());

        return dto;
    }

    public UserDetailResponse toUserDetailDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDetailResponse dto = new UserDetailResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setUsername(user.getUsername());
        dto.setStatus(user.getStatus());
        dto.setRoleCodes(extractRoleCodes(user.getRoles()));
        dto.setRegistrationDate(user.getCreatedAt());
        dto.setLastLoginDate(user.getLastLoginDate());
        
        // Additional fields for detail DTO
        dto.setPhone(user.getPhone());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setEmailVerified(user.getEmailVerified());
        dto.setLastPasswordChangeDate(user.getLastPasswordChangeDate());
        dto.setPermissions(user.getAllPermissions());

        return dto;
    }

    private Set<String> extractRoleCodes(Set<Role> roles) {
        if (roles == null) {
            return Set.of();
        }
        return roles.stream()
                .map(Role::getCode)
                .collect(Collectors.toSet());
    }
}
