package leets.leenk.domain.notification.application.mapper

import leets.leenk.domain.notification.application.dto.response.NotificationCountResponse
import leets.leenk.domain.notification.application.dto.response.NotificationListResponse
import leets.leenk.domain.notification.application.dto.response.NotificationResponse
import leets.leenk.domain.notification.domain.entity.NotificationEntity
import leets.leenk.global.common.dto.PageableMapperUtil
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component

@Component
class NotificationResponseMapper {

    fun toNotificationListResponse(notifications: Slice<NotificationEntity>): NotificationListResponse =
        NotificationListResponse(
            notificationResponses = notifications.map { toResponse(it) }.toList(),
            pageable = PageableMapperUtil.from(notifications),
        )

    fun toResponse(notification: NotificationEntity): NotificationResponse =
        NotificationResponse(
            id = notification.id!!,
            userId = notification.userId,
            notificationType = notification.notificationType,
            path = notification.content.path,
            isRead = notification.isRead,
            content = notification.content,
            updateDate = notification.updateDate,
        )

    fun toCountResponse(count: Long): NotificationCountResponse =
        NotificationCountResponse(notificationCount = count)
}
