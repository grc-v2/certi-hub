package com.certimaster.identity.application.command;

import com.certimaster.identity.adapter.web.response.ProfileResponse;
import com.certimaster.identity.domain.entity.User;
import com.certimaster.identity.adapter.persistence.UserWriteRepository;
import com.certimaster.identity.domain.service.AvatarStorageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class ProfileCommandService {


    private final UserWriteRepository userRepository;
    private final AvatarStorageService avatarStorageService;

    public ProfileCommandService(UserWriteRepository userRepository, AvatarStorageService avatarStorageService) {
        this.userRepository = userRepository;
        this.avatarStorageService = avatarStorageService;
    }

    @Transactional
    public ProfileResponse updateProfile(UpdateProfileCommand command) {
        User user = findUser(command.userId());

        if (command.displayName() != null) user.setFullName(command.displayName());
        if (command.bio() != null) user.setBio(command.bio());
        if (command.timezone() != null) user.setTimezone(command.timezone());
        if (command.preferredLanguage() != null) user.setPreferredLanguage(command.preferredLanguage());
        if (command.professionalInfo() != null) user.setProfessionalInfo(command.professionalInfo());
        if (command.learningPreferences() != null) user.setLearningPreferences(command.learningPreferences());

        userRepository.save(user);
        return buildResponse(user);
    }

    @Transactional
    public String uploadAvatar(UploadAvatarCommand command) {
        User user = findUser(command.userId());
        String avatarUrl = avatarStorageService.uploadAvatar(command.userId(), command.file());
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        return avatarUrl;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
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
