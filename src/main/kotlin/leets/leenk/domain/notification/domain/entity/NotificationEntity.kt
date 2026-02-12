package leets.leenk.domain.notification.domain.entity

import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "notifications")
@CompoundIndex(
    name = "unique_notification",
    def = "{'userId': 1, 'notificationType': 1, 'content.targetId': 1, 'deleteDate': 1}",
    unique = true,
)
data class NotificationEntity(
    @Id
    val id: String? = null,
    var userId: Long,
    var notificationType: NotificationType,
    var content: NotificationPayload,
    var isRead: Boolean = false,
    // TODO: 마이그레이션 이후 몽고 BaseEntity 상속하도록 수정
    @CreatedDate
    val createDate: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    var updateDate: LocalDateTime = LocalDateTime.now(),
    var deleteDate: LocalDateTime? = null,
) {
    fun markRead() {
        this.isRead = true
        this.updateDate = LocalDateTime.now()
    }

    fun markUnread() {
        this.isRead = false
        this.updateDate = LocalDateTime.now()
    }

    fun updateContent(
        newTitle: String,
        newBody: String,
        newMetadata: Map<String, Any>,
    ): NotificationEntity =
        NotificationEntity(
            id = this.id,
            userId = this.userId,
            notificationType = this.notificationType,
            content =
                NotificationPayload(
                    title = newTitle,
                    body = newBody,
                    path = this.content.path,
                    targetId = this.content.targetId,
                    metadata = newMetadata,
                ),
            isRead = false,
            createDate = this.createDate, // 기존 생성일 유지
            updateDate = LocalDateTime.now(), // 수정일 갱신
        )
}
