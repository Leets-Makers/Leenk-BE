package leets.leenk.domain.notification.application.mapper

import leets.leenk.domain.notification.application.dto.response.LeenkStartingSoonInfo
import leets.leenk.domain.notification.application.dto.response.NotificationCountResponse
import leets.leenk.domain.notification.application.dto.response.NotificationDetailResponse
import leets.leenk.domain.notification.application.dto.response.NotificationListResponse
import leets.leenk.domain.notification.application.dto.response.NotificationResponse
import leets.leenk.domain.notification.domain.entity.Notification
import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import leets.leenk.global.common.dto.PageableMapperUtil
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.Date

@Component
class NotificationResponseMapper {
    fun toNotificationListResponse(notifications: Slice<Notification>): NotificationListResponse =
        NotificationListResponse(
            notificationResponses = notifications.map { toResponse(it) }.toList(),
            pageable = PageableMapperUtil.from(notifications),
        )

    fun toResponse(notification: Notification): NotificationResponse =
        NotificationResponse(
            deepLink = notification.content.path,
            title = notification.content.title,
            body = notification.content.body,
            createDate = notification.createDate,
            updateDate = notification.updateDate,
            details = extractDetails(notification),
            leenkStartingSoonInfo = extractLeenkStartingSoonInfo(notification),
        )

    fun toCountResponse(count: Long): NotificationCountResponse = NotificationCountResponse(notificationCount = count)

    @Suppress("UNCHECKED_CAST")
    private fun extractDetails(notification: Notification): List<NotificationDetailResponse>? =
        (notification.content.metadata["details"] as? List<Map<String, Any>>)
            ?.map { detail ->
                NotificationDetailResponse(
                    userId = (detail["userId"] as? Number)?.toLong(),
                    name = detail["name"] as? String,
                    milestone = (detail["milestone"] as? Number)?.toLong(),
                    body = detail["body"] as? String ?: "",
                    createDate = detail["createDate"].toDateString(),
                )
            }?.takeIf { it.isNotEmpty() }

    private fun extractLeenkStartingSoonInfo(notification: Notification): LeenkStartingSoonInfo? {
        if (notification.notificationType != NotificationType.LEENK_STARTING_SOON) return null

        val metadata = notification.content.metadata

        return LeenkStartingSoonInfo(
            startTime = metadata["startTime"] as? String,
            placeName = metadata["placeName"] as? String,
        )
    }

    private fun Any?.toDateString(): String =
        when (this) {
            is LocalDateTime -> {
                this.toString()
            }

            is Date -> {
                this
                    .toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime()
                    .toString()
            }

            else -> {
                this?.toString() ?: ""
            }
        }
}
