package leets.leenk.domain.notification.application.dto

import leets.leenk.domain.notification.domain.entity.enums.NotificationType

data class NotificationRequest(
    val userId: Long,
    val type: NotificationType,
    val targetId: Long,
    val dynamicParams: List<Any> = emptyList(), // {name}, {count} 등 body 치환용
    val metadata: Map<String, Any> = emptyMap(),
) {
    val title: String
        get() = type.title

    val body: String
        get() = type.formatContent(*dynamicParams.toTypedArray())

    val path: String
        get() = "${type.path}/$targetId"
}
