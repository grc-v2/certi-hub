package com.certimaster.identity.application.command;

import com.certimaster.shared.exception.BusinessException;
import com.certimaster.shared.util.InputSanitizer;
import com.certimaster.identity.adapter.persistence.RoleRepository;
import com.certimaster.identity.application.command.SessionService;
import com.certimaster.identity.adapter.persistence.UserWriteRepository;
import com.certimaster.identity.adapter.web.mapper.UserMapper;
import com.certimaster.identity.adapter.web.response.UserDetailResponse;
import com.certimaster.identity.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserWriteRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private SessionService sessionService;

    @Mock
    private ApplicationEventPublisher applicationEvents;

    @Mock
    private UserMapper userMapper;

    private AdminUserService service;

    @BeforeEach
    void setUp() {
        service = new AdminUserService(
                userRepository,
                roleRepository,
                sessionService,
                applicationEvents,
                userMapper,
                new InputSanitizer()
        );
    }

    @Test
    void updateUserFailsWhenEmailAlreadyExists() {
        User user = activeUser();
        when(userRepository.findByIdAndDeletedAtIsNull(42L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.updateUser(new UpdateAdminUserCommand(
                42L, "new@example.com", null, null, null, null
        ))).isInstanceOf(BusinessException.class);

        verify(userRepository, never()).save(any(User.class));
        verify(applicationEvents, never()).publishEvent(any());
    }

    @Test
    void updateUserPublishesChangedFieldsEvent() {
        User user = activeUser();
        UserDetailResponse response = new UserDetailResponse();
        when(userRepository.findByIdAndDeletedAtIsNull(42L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDetailDTO(user)).thenReturn(response);

        UserDetailResponse result = service.updateUser(new UpdateAdminUserCommand(
                42L, null, "Updated User", null, null, null
        ));

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(applicationEvents).publishEvent(eventCaptor.capture());
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) ReflectionTestUtils.invokeMethod(eventCaptor.getValue(), "data");
        assertThat(result).isSameAs(response);
        assertThat(user.getFullName()).isEqualTo("Updated User");
        assertThat((String) ReflectionTestUtils.invokeMethod(eventCaptor.getValue(), "eventType")).isEqualTo("user_updated");
        assertThat((Long) ReflectionTestUtils.invokeMethod(eventCaptor.getValue(), "userId")).isEqualTo(42L);
        assertThat(payload).containsEntry("userId", 42L);
        assertThat(payload).containsKey("changedFields");
    }

    @Test
    void deactivateUserRevokesSessionsAndPublishesStatusEvent() {
        User user = activeUser();
        when(userRepository.findByIdAndDeletedAtIsNull(42L)).thenReturn(Optional.of(user));
        when(sessionService.forceLogoutAllSessions(42L, "DEACTIVATED")).thenReturn(2);

        service.deactivateUser(new ChangeAdminUserStatusCommand(42L, "DISABLED"));

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(userRepository).save(user);
        verify(applicationEvents).publishEvent(eventCaptor.capture());
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) ReflectionTestUtils.invokeMethod(eventCaptor.getValue(), "data");
        assertThat(user.getStatus()).isEqualTo("DISABLED");
        assertThat((String) ReflectionTestUtils.invokeMethod(eventCaptor.getValue(), "eventType")).isEqualTo("account_status_changed");
        assertThat(payload).containsEntry("oldStatus", "ACTIVE");
        assertThat(payload).containsEntry("newStatus", "DISABLED");
        assertThat(payload).containsEntry("terminatedSessions", 2);
    }

    private User activeUser() {
        User user = User.builder()
                .email("old@example.com")
                .username("learner")
                .passwordHash("hashed")
                .fullName("Learner User")
                .status("ACTIVE")
                .build();
        user.setId(42L);
        return user;
    }
}
