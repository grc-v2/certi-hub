package com.certimaster.identity.application.command;

import java.util.List;

public record BulkChangeUserStatusCommand(
        List<Long> userIds,
        String status
) {
}
