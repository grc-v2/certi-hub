package com.certimaster.engagement.application.listener;

import com.certimaster.engagement.application.command.NotificationCommandService;
import com.certimaster.shared.event.BroadcastRecipientsResolvedEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Final step of the broadcast flow: once identity has resolved the recipient ids,
 * engagement (which owns notifications) persists one notification per user.
 */
@Component
public class BroadcastNotificationCreator {

    private final NotificationCommandService notificationCommandService;

    public BroadcastNotificationCreator(NotificationCommandService notificationCommandService) {
        this.notificationCommandService = notificationCommandService;
    }

    @ApplicationModuleListener
    public void onRecipientsResolved(BroadcastRecipientsResolvedEvent event) {
        if (event.userIds() == null || event.userIds().isEmpty()) {
            return;
        }
        notificationCommandService.createForRecipients(
                event.userIds(), event.type(), event.title(), event.message());
    }
}
