package com.certimaster.identity.adapter.web;

import com.certimaster.shared.security.SecurityUtils;
import com.certimaster.identity.adapter.web.request.UpdateProfileRequest;
import com.certimaster.identity.adapter.web.response.ProfileResponse;
import com.certimaster.identity.application.command.ProfileCommandService;
import com.certimaster.identity.application.command.UpdateProfileCommand;
import com.certimaster.identity.application.command.UploadAvatarCommand;
import com.certimaster.identity.application.query.GetProfileQuery;
import com.certimaster.identity.application.query.ProfileQueryService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;

    public ProfileController(ProfileCommandService profileCommandService, ProfileQueryService profileQueryService) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(profileQueryService.getProfile(new GetProfileQuery(userId)));
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateMyProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(profileCommandService.updateProfile(toUpdateProfileCommand(userId, request)));
    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = SecurityUtils.getCurrentUserId();
        String avatarUrl = profileCommandService.uploadAvatar(new UploadAvatarCommand(userId, file));
        return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfileResponse> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(profileQueryService.getProfile(new GetProfileQuery(userId)));
    }

    private UpdateProfileCommand toUpdateProfileCommand(Long userId, UpdateProfileRequest request) {
        return new UpdateProfileCommand(
                userId,
                request.getDisplayName(),
                request.getBio(),
                request.getTimezone(),
                request.getPreferredLanguage(),
                request.getProfessionalInfo(),
                request.getLearningPreferences()
        );
    }
}
