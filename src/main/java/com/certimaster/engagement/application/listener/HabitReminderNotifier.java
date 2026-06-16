package com.certimaster.engagement.application.listener;

import com.certimaster.engagement.application.command.CreateNotificationCommand;
import com.certimaster.engagement.application.command.NotificationCommandService;
import com.certimaster.productivity.domain.event.HabitReminderDueEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Turns a productivity habit reminder into an in-app notification for the user.
 * Keeps the cross-module flow one-directional: productivity publishes the event,
 * engagement (which owns notifications) consumes it.
 */
@Component
public class HabitReminderNotifier {

    private final NotificationCommandService notificationCommandService;

    public HabitReminderNotifier(NotificationCommandService notificationCommandService) {
        this.notificationCommandService = notificationCommandService;
    }

    @ApplicationModuleListener
    public void onHabitReminderDue(HabitReminderDueEvent event) {
        if (event.getUserId() == null) {
            return;
        }
        String habitName = event.getHabitName() != null ? event.getHabitName() : "thói quen";
        String message = "Đừng quên \"" + habitName + "\" hôm nay nhé!";
        String relatedEntityId = event.getHabitId() != null ? String.valueOf(event.getHabitId()) : null;

        notificationCommandService.create(new CreateNotificationCommand(
                event.getUserId(), "HABIT_REMINDER", "Nhắc nhở thói quen", message, relatedEntityId));
    }
}
