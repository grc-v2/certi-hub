package com.certimaster.identity.application.command;

import com.certimaster.identity.adapter.persistence.UserWriteRepository;
import com.certimaster.identity.domain.entity.User;
import com.certimaster.identity.domain.service.AvatarStorageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileCommandServiceTest {

    @Mock
    private UserWriteRepository userRepository;

    @Mock
    private AvatarStorageService avatarStorageService;

    @InjectMocks
    private ProfileCommandService service;

    @Test
    void updateProfileUpdatesEditableFieldsAndSavesUser() {
        User user = baseUser();
        when(userRepository.findById(42L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        var response = service.updateProfile(new UpdateProfileCommand(
                42L,
                "Updated User",
                "Cloud architect",
                "Asia/Ho_Chi_Minh",
                "vi",
                Map.of("title", "Engineer"),
                Map.of("dailyGoal", 30)
        ));

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedUser.capture());
        assertThat(savedUser.getValue().getFullName()).isEqualTo("Updated User");
        assertThat(savedUser.getValue().getBio()).isEqualTo("Cloud architect");
        assertThat(savedUser.getValue().getTimezone()).isEqualTo("Asia/Ho_Chi_Minh");
        assertThat(savedUser.getValue().getPreferredLanguage()).isEqualTo("vi");
        assertThat(savedUser.getValue().getProfessionalInfo()).containsEntry("title", "Engineer");
        assertThat(savedUser.getValue().getLearningPreferences()).containsEntry("dailyGoal", 30);
        assertThat(response.getDisplayName()).isEqualTo("Updated User");
        assertThat(response.getCompletenessScore()).isEqualTo(100);
    }

    @Test
    void uploadAvatarStoresFileAndPersistsAvatarUrl() {
        User user = baseUser();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        when(userRepository.findById(42L)).thenReturn(Optional.of(user));
        when(avatarStorageService.uploadAvatar(42L, file)).thenReturn("http://cdn/avatar.png");

        String avatarUrl = service.uploadAvatar(new UploadAvatarCommand(42L, file));

        assertThat(avatarUrl).isEqualTo("http://cdn/avatar.png");
        assertThat(user.getAvatarUrl()).isEqualTo("http://cdn/avatar.png");
        verify(userRepository).save(user);
    }

    @Test
    void updateProfileFailsWhenUserDoesNotExist() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateProfile(new UpdateProfileCommand(
                42L, "Updated User", null, null, null, null, null
        ))).isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found: 42");
    }

    private User baseUser() {
        User user = User.builder()
                .email("learner@example.com")
                .username("learner")
                .passwordHash("hashed")
                .fullName("Learner User")
                .avatarUrl("http://cdn/existing.png")
                .phone("0900000000")
                .build();
        user.setId(42L);
        return user;
    }
}
