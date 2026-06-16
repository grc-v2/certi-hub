package com.certimaster.shared.event;

/**
 * Raised when an admin requests an in-app broadcast. Lives in the shared (open)
 * module so it can cross the engagement → identity boundary: engagement publishes
 * the intent, identity resolves the recipients (it owns users), and engagement
 * then creates one notification per recipient. Keeps the two modules decoupled —
 * neither depends on the other, only on this shared event type.
 *
 * @param targetRole role code to target (e.g. "ADMIN", "USER"); {@code null}/blank = all active users
 */
public record BroadcastNotificationRequestedEvent(
        String title,
        String message,
        String type,
        String targetRole) {
}
