package com.certimaster.learning.domain.service;

import com.certimaster.shared.exception.ForbiddenException;
import com.certimaster.shared.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ResultAuthorizationService {

    private static final Logger log = LoggerFactory.getLogger(ResultAuthorizationService.class);

    public void validateUserAuthorization(Long resourceOwnerId, Long currentUserId) {
        if (!resourceOwnerId.equals(currentUserId) && !SecurityUtils.isAdmin()) {
            log.warn("User {} attempted to access resource owned by user {}", currentUserId, resourceOwnerId);
            throw ForbiddenException.resourceOwnerOnly();
        }
    }
}
