package leets.leenk.domain.notification.domain.service

import leets.leenk.domain.notification.domain.entity.NotificationEntity
import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import leets.leenk.domain.notification.domain.repository.NotificationEntityRepository
import org.springframework.stereotype.Service

@Service
class NotificationEntityGetService(
    private val notificationEntityRepository: NotificationEntityRepository,
) {
    fun findByUserIdAndTypeAndTargetId(
        userId: Long,
        type: NotificationType,
        targetId: Long,
    ): NotificationEntity? =
        notificationEntityRepository.findNotificationByUserAndTypeAndTarget(
            userId = userId,
            notificationType = type,
            targetId = targetId,
        )

    fun findAllByUserId(userId: Long): List<NotificationEntity> = notificationEntityRepository.findAllByUserId(userId)

    /**
     * 특정 사용자의 첫 공감 알림이 이미 존재하는지 확인
     */
    fun checkFirstReactionDuplicated(
        feedAuthorId: Long,
        feedId: Long,
        reactorId: Long,
    ): Boolean =
        notificationEntityRepository.findByFeedIdAndUserIdInFirstReactions(
            feedAuthorId = feedAuthorId,
            notificationType = NotificationType.FEED_FIRST_REACTION,
            feedId = feedId,
            reactorId = reactorId,
        ) != null

    /**
     * 특정 마일스톤이 details에 이미 존재하는지 확인
     */
    fun checkReactionCountDuplicated(
        feedAuthorId: Long,
        feedId: Long,
        milestone: Int,
    ): Boolean {
        val notification =
            notificationEntityRepository.findNotificationByUserAndTypeAndTarget(
                userId = feedAuthorId,
                notificationType = NotificationType.FEED_REACTION_COUNT,
                targetId = feedId,
            ) ?: return false

        val details =
            (notification.content.metadata["details"] as? List<*>)?.filterIsInstance<Map<String, Any>>()
                ?: return false

        return details.any { detail ->
            (detail["milestone"] as? Int) == milestone
        }
    }
}
