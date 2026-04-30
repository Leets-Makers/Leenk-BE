package leets.leenk.domain.notification.application.dto

import leets.leenk.domain.notification.domain.entity.enums.NotificationType

data class NotificationRequest(
    val userId: Long,
    val type: NotificationType,
    val targetId: Long,
    val name: String? = null,
    val title: String? = null,
    val count: Long? = null,
    val metadata: Map<String, Any> = emptyMap(),
) {
    val notificationTitle: String
        get() = type.title

    val body: String
        get() = type.formatContent(name = name, title = title, count = count)

    val path: String
        get() = "${type.path}/$targetId"
}
