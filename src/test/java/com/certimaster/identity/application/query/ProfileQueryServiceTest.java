package com.certimaster.identity.application.query;

import com.certimaster.identity.adapter.persistence.UserReadRepository;
import com.certimaster.identity.domain.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileQueryServiceTest {

    @Mock
    private UserReadRepository userRepository;

    @InjectMocks
    private ProfileQueryService service;

    @Test
    void getProfileReturnsProfileResponse() {
        User user = User.builder()
                .email("learner@example.com")
                .username("learner")
                .passwordHash("hashed")
                .fullName("Learner User")
                .bio("Cloud architect")
                .timezone("Asia/Ho_Chi_Minh")
                .preferredLanguage("vi")
                .professionalInfo(Map.of("title", "Engineer"))
                .learningPreferences(Map.of("dailyGoal", 30))
                .build();
        user.setId(42L);
        when(userRepository.findById(42L)).thenReturn(Optional.of(user));

        var response = service.getProfile(new GetProfileQuery(42L));

        assertThat(response.getUserId()).isEqualTo(42L);
        assertThat(response.getEmail()).isEqualTo("learner@example.com");
        assertThat(response.getDisplayName()).isEqualTo("Learner User");
        assertThat(response.getProfessionalInfo()).containsEntry("title", "Engineer");
        assertThat(response.getLearningPreferences()).containsEntry("dailyGoal", 30);
    }

    @Test
    void getProfileFailsWhenUserDoesNotExist() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getProfile(new GetProfileQuery(42L)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found: 42");
    }
}
