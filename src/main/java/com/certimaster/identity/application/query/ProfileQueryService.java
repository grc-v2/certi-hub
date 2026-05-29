package com.certimaster.identity.application.query;

import com.certimaster.identity.adapter.persistence.UserReadRepository;
import com.certimaster.identity.adapter.web.response.ProfileResponse;
import com.certimaster.identity.domain.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ProfileQueryService {

    private final UserReadRepository userRepository;

    public ProfileQueryService(UserReadRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ProfileResponse getProfile(GetProfileQuery query) {
        User user = userRepository.findById(query.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + query.userId()));
        return buildResponse(user);
    }

    private ProfileResponse buildResponse(User user) {
        ProfileResponse response = new ProfileResponse();
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setDisplayName(user.getFullName());
        response.setBio(user.getBio());
        response.setTimezone(user.getTimezone());
        response.setPreferredLanguage(user.getPreferredLanguage());
        response.setProfessionalInfo(user.getProfessionalInfo() != null ? user.getProfessionalInfo() : Map.of());
        response.setLearningPreferences(user.getLearningPreferences() != null ? user.getLearningPreferences() : Map.of());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setCompletenessScore(calculateCompleteness(user));
        return response;
    }

    private int calculateCompleteness(User user) {
        int filled = 0;
        int total = 7;
        if (isNotEmpty(user.getFullName())) filled++;
        if (isNotEmpty(user.getAvatarUrl())) filled++;
        if (isNotEmpty(user.getPhone())) filled++;
        if (isNotEmpty(user.getBio())) filled++;
        if (isNotEmpty(user.getTimezone())) filled++;
        if (isNotEmpty(user.getPreferredLanguage())) filled++;
        if (isNotEmptyMap(user.getProfessionalInfo())) filled++;
        return (int) Math.round((double) filled / total * 100);
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.isBlank();
    }

    private boolean isNotEmptyMap(Map<String, Object> map) {
        return map != null && !map.isEmpty();
    }
}
