package com.certimaster.identity.application.listener;

import com.certimaster.identity.adapter.persistence.UserReadRepository;
import com.certimaster.shared.event.BroadcastNotificationRequestedEvent;
import com.certimaster.shared.event.BroadcastRecipientsResolvedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Resolves an admin broadcast request into concrete recipient ids. Identity owns
 * users, so it answers "who", while engagement owns "what" (the notifications).
 * The two never depend on each other directly — only on the shared events.
 */
@Component
public class BroadcastRecipientResolver {

    private final UserReadRepository userReadRepository;
    private final ApplicationEventPublisher applicationEvents;

    public BroadcastRecipientResolver(UserReadRepository userReadRepository,
                                      ApplicationEventPublisher applicationEvents) {
        this.userReadRepository = userReadRepository;
        this.applicationEvents = applicationEvents;
    }

    @ApplicationModuleListener
    public void onBroadcastRequested(BroadcastNotificationRequestedEvent event) {
        String role = event.targetRole();
        List<Long> recipients = (role == null || role.isBlank())
                ? userReadRepository.findActiveUserIds()
                : userReadRepository.findActiveUserIdsByRole(role);

        if (recipients.isEmpty()) {
            return;
        }

        applicationEvents.publishEvent(new BroadcastRecipientsResolvedEvent(
                recipients, event.type(), event.title(), event.message()));
    }
}
