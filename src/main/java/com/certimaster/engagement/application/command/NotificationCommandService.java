package com.certimaster.engagement.application.command;

import com.certimaster.engagement.adapter.persistence.NotificationRepository;
import com.certimaster.engagement.domain.entity.Notification;
import com.certimaster.shared.event.BroadcastNotificationRequestedEvent;
import com.certimaster.shared.exception.ResourceNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationCommandService {

    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher applicationEvents;

    public NotificationCommandService(NotificationRepository notificationRepository,
                                      ApplicationEventPublisher applicationEvents) {
        this.notificationRepository = notificationRepository;
        this.applicationEvents = applicationEvents;
    }

    /** Create a notification for a single recipient. Used internally by event listeners. */
    public Long create(CreateNotificationCommand command) {
        Notification notification = Notification.builder()
                .userId(command.userId())
                .type(command.type())
                .title(command.title())
                .message(command.message())
                .relatedEntityId(command.relatedEntityId())
                .build();
        return notificationRepository.save(notification).getId();
    }

    /**
     * Request an admin broadcast. Recipients are resolved asynchronously by identity
     * (it owns users); fan-out happens via {@link BroadcastNotificationRequestedEvent}.
     */
    public void requestBroadcast(BroadcastNotificationRequestedEvent event) {
        applicationEvents.publishEvent(event);
    }

    /** Persist the same notification for many recipients in one batch. */
    public void createForRecipients(List<Long> userIds, String type, String title, String message) {
        List<Notification> notifications = userIds.stream()
                .map(userId -> Notification.builder()
                        .userId(userId)
                        .type(type)
                        .title(title)
                        .message(message)
                        .build())
                .toList();
        notificationRepository.saveAll(notifications);
    }

    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> ResourceNotFoundException.byId("Notification", notificationId));
        notification.markRead();
        notificationRepository.save(notification);
    }

    public void markAllAsRead(Long userId) {
        notificationRepository.markAllReadForUser(userId);
    }
}
