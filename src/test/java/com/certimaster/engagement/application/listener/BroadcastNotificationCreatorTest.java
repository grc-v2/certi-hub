package com.certimaster.engagement.application.listener;

import com.certimaster.engagement.application.command.NotificationCommandService;
import com.certimaster.shared.event.BroadcastRecipientsResolvedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BroadcastNotificationCreatorTest {

    @Mock
    private NotificationCommandService notificationCommandService;

    @InjectMocks
    private BroadcastNotificationCreator listener;

    @Test
    void resolvedRecipientsAreFannedOutToNotificationService() {
        listener.onRecipientsResolved(new BroadcastRecipientsResolvedEvent(
                List.of(1L, 2L), "BROADCAST", "Bảo trì", "22h tối nay"));

        verify(notificationCommandService).createForRecipients(
                List.of(1L, 2L), "BROADCAST", "Bảo trì", "22h tối nay");
    }

    @Test
    void emptyRecipientListIsIgnored() {
        listener.onRecipientsResolved(new BroadcastRecipientsResolvedEvent(
                List.of(), "BROADCAST", "t", "m"));

        verify(notificationCommandService, never())
                .createForRecipients(anyList(), anyString(), anyString(), any());
    }

    @Test
    void nullRecipientListIsIgnored() {
        listener.onRecipientsResolved(new BroadcastRecipientsResolvedEvent(
                null, "BROADCAST", "t", "m"));

        verify(notificationCommandService, never())
                .createForRecipients(anyList(), anyString(), anyString(), any());
    }
}
