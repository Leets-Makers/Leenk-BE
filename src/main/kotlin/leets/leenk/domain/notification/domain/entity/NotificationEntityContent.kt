package leets.leenk.domain.notification.domain.entity

data class NotificationEntityContent(
    val title: String,
    val body: String,
    val targetId: Long,
    val metadata: Map<String, Any> = emptyMap()
)
