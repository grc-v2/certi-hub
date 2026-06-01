package com.certimaster.identity.application.query;

import com.certimaster.identity.domain.entity.User;
import com.certimaster.identity.adapter.persistence.UserReadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class PermissionService {


    private final UserReadRepository userRepository;

    public PermissionService(UserReadRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Set<String> getUserPermissions(Long userId) {

        if (userId == null) {
            return Set.of();
        }
        return userRepository.findByIdWithRoles(userId)
                .map(User::getAllPermissions)
                .orElseGet(Set::of);
    }
}
