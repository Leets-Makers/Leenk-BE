package leets.leenk.domain.notification.domain.entity

data class NotificationPayload(
    val title: String,
    val body: String,
    val path: String,
    val targetId: Long,
    val metadata: Map<String, Any> = emptyMap(),
)
