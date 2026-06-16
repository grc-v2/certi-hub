package com.certimaster.shared.event;

import java.util.List;

/**
 * Raised by identity once it has resolved the recipients of a broadcast back into
 * concrete user ids. Engagement consumes this to fan out one notification per user.
 * Second half of the {@link BroadcastNotificationRequestedEvent} flow.
 */
public record BroadcastRecipientsResolvedEvent(
        List<Long> userIds,
        String type,
        String title,
        String message) {
}
