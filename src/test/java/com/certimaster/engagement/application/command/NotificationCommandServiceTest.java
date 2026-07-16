package com.certimaster.engagement.application.command;

import com.certimaster.engagement.adapter.persistence.NotificationRepository;
import com.certimaster.engagement.domain.entity.Notification;
import com.certimaster.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationCommandServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ApplicationEventPublisher applicationEvents;

    @InjectMocks
    private NotificationCommandService service;

    @Test
    void createPersistsUnreadNotificationAndReturnsId() {
        when(notificationRepository.save(any())).thenAnswer(inv -> {
            Notification n = inv.getArgument(0);
            n.setId(11L);
            return n;
        });

        Long id = service.create(new CreateNotificationCommand(
                42L, "EXAM_COMPLETED", "Kết quả thi", "Bạn đạt 85%", "session-9"));

        assertThat(id).isEqualTo(11L);
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(42L);
        assertThat(saved.getType()).isEqualTo("EXAM_COMPLETED");
        assertThat(saved.getTitle()).isEqualTo("Kết quả thi");
        assertThat(saved.getMessage()).isEqualTo("Bạn đạt 85%");
        assertThat(saved.getRelatedEntityId()).isEqualTo("session-9");
        assertThat(saved.isRead()).isFalse();
    }

    @Test
    void createForRecipientsFansOutOneNotificationPerUser() {
        service.createForRecipients(List.of(1L, 2L, 3L), "BROADCAST", "Bảo trì", "Hệ thống bảo trì 22h");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass((Class) List.class);
        verify(notificationRepository).saveAll(captor.capture());
        List<Notification> saved = captor.getValue();
        assertThat(saved).hasSize(3);
        assertThat(saved).extracting(Notification::getUserId).containsExactly(1L, 2L, 3L);
        assertThat(saved).allSatisfy(n -> {
            assertThat(n.getType()).isEqualTo("BROADCAST");
            assertThat(n.getTitle()).isEqualTo("Bảo trì");
            assertThat(n.isRead()).isFalse();
        });
    }

    @Test
    void markAsReadMarksOwnedNotification() {
        Notification notification = Notification.builder().userId(42L).type("X").title("t").build();
        when(notificationRepository.findByIdAndUserId(11L, 42L)).thenReturn(Optional.of(notification));

        service.markAsRead(11L, 42L);

        assertThat(notification.isRead()).isTrue();
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAsReadOnAnotherUsersNotificationFails() {
        when(notificationRepository.findByIdAndUserId(11L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.markAsRead(11L, 7L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(notificationRepository, never()).save(any());
    }
}
