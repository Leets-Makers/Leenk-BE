package leets.leenk.domain.notification.domain.service

import leets.leenk.domain.notification.application.exception.InvalidNotificationAccessException
import leets.leenk.domain.notification.application.exception.NotificationNotFoundException
import leets.leenk.domain.notification.domain.entity.NotificationEntity
import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import leets.leenk.domain.notification.domain.repository.NotificationEntityRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
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

    fun findPageByUserId(
        userId: Long,
        pageable: Pageable,
    ): Slice<NotificationEntity> = notificationEntityRepository.findPageByUserIdAndDeleteDateIsNull(userId, pageable)

    fun countUnreadByUserId(userId: Long): Long = notificationEntityRepository.countUnreadByUserId(userId)

    fun findByIdForUser(
        notificationId: String,
        userId: Long,
    ): NotificationEntity {
        val notification =
            notificationEntityRepository.findActiveById(notificationId)
                ?: throw NotificationNotFoundException()

        if (notification.userId != userId) throw InvalidNotificationAccessException()

        return notification
    }

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
}
