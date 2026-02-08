package leets.leenk.domain.notification.application.dto

import leets.leenk.domain.notification.domain.entity.enums.NotificationType

data class NotificationRequest(
    val userId: Long,
    val type: NotificationType,
    val title: String,
    val body: String,
    val targetId: Long,
    val metadata: Map<String, Any> = emptyMap()
)
