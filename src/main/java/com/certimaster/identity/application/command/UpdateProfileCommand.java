package com.certimaster.identity.application.command;

import java.util.Map;

public record UpdateProfileCommand(
        Long userId,
        String displayName,
        String bio,
        String timezone,
        String preferredLanguage,
        Map<String, Object> professionalInfo,
        Map<String, Object> learningPreferences
) {
}
