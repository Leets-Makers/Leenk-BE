package leets.leenk.domain.notification.domain.entity

import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import leets.leenk.global.common.entity.MongoBaseEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "notifications")
data class NotificationEntity(
    @Id
    val id: String? = null,
    var userId: Long,
    var notificationType: NotificationType,
    var content: NotificationPayload,
    var isRead: Boolean = false
): MongoBaseEntity() {
    fun markRead() {
        this.isRead = true
    }

    fun markUnread() {
        this.isRead = false
    }
}
