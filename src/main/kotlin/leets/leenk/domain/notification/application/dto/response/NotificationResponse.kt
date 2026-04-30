package leets.leenk.domain.notification.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude
import leets.leenk.domain.notification.domain.entity.NotificationPayload
import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class NotificationResponse(
    val id: String,
    val userId: Long,
    val notificationType: NotificationType,
    val path: String,
    val isRead: Boolean,
    val content: NotificationPayload,
    val updateDate: LocalDateTime,
)
