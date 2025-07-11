package leets.leenk.domain.notification.application.mapper;

import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import leets.leenk.domain.notification.application.dto.response.NotificationCountResponse;
import leets.leenk.domain.notification.application.dto.response.NotificationListResponse;
import leets.leenk.domain.notification.application.dto.response.NotificationResponse;
import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.global.common.dto.PageableMapperUtil;

@Component
public class NotificationResponseMapper {

    public NotificationListResponse toNotificationListResponse(Slice<Notification> notifications) {
        List<NotificationResponse> responses = notifications
                .map(this::toResponse)
                .toList();

        return NotificationListResponse.builder()
                .notificationResponses(responses)
                .pageable(PageableMapperUtil.from(notifications))
                .build();
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .notificationType(notification.getNotificationType())
                .isRead(notification.getIsRead())
                .content(notification.getContent())
                .updateDate(notification.getUpdateDate())
                .build();
    }

    public NotificationCountResponse toCountResponse(long notificationCount) {
        return NotificationCountResponse.builder()
                .notificationCount(notificationCount)
                .build();
    }
}
