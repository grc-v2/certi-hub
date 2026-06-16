package com.certimaster.engagement.adapter.web.mapper;

import com.certimaster.engagement.adapter.web.response.NotificationResponse;
import com.certimaster.engagement.domain.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        if (notification == null) {
            return null;
        }
        NotificationResponse r = new NotificationResponse();
        r.setId(notification.getId());
        r.setType(notification.getType());
        r.setTitle(notification.getTitle());
        r.setMessage(notification.getMessage());
        r.setRead(notification.isRead());
        r.setCreatedAt(notification.getCreatedAt() != null ? notification.getCreatedAt().toString() : null);
        r.setRelatedEntityId(notification.getRelatedEntityId());
        return r;
    }
}
