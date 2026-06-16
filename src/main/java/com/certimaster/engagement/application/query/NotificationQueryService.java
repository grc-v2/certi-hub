package com.certimaster.engagement.application.query;

import com.certimaster.engagement.adapter.persistence.NotificationRepository;
import com.certimaster.engagement.adapter.web.mapper.NotificationMapper;
import com.certimaster.engagement.adapter.web.response.NotificationResponse;
import com.certimaster.engagement.domain.entity.Notification;
import com.certimaster.shared.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationQueryService(NotificationRepository notificationRepository,
                                    NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    public PageResponse<NotificationResponse> list(ListNotificationsQuery query) {
        PageRequest pageable = PageRequest.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> page = query.unreadOnly()
                ? notificationRepository.findByUserIdAndReadFalse(query.userId(), pageable)
                : notificationRepository.findByUserId(query.userId(), pageable);
        return PageResponse.of(page.map(notificationMapper::toResponse));
    }
}
