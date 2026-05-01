package leets.leenk.domain.notification.domain.service

import leets.leenk.domain.notification.application.exception.InvalidNotificationAccessException
import leets.leenk.domain.notification.application.exception.NotificationNotFoundException
import leets.leenk.domain.notification.domain.entity.Notification
import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import leets.leenk.domain.notification.domain.repository.NotificationRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class NotificationGetService(
    private val notificationRepository: NotificationRepository,
) {
    fun findByUserIdAndTypeAndTargetId(
        userId: Long,
        type: NotificationType,
        targetId: Long,
    ): Notification? =
        notificationRepository.findNotificationByUserAndTypeAndTarget(
            userId = userId,
            notificationType = type,
            targetId = targetId,
        )

    fun findAllByUserId(userId: Long): List<Notification> = notificationRepository.findAllByUserId(userId)

    fun findPageByUserId(
        userId: Long,
        pageable: Pageable,
    ): Slice<Notification> = notificationRepository.findPageByUserIdAndDeleteDateIsNull(userId, pageable)

    fun countUnreadByUserId(userId: Long): Long = notificationRepository.countUnreadByUserId(userId)

    fun findByIdForUser(
        notificationId: String,
        userId: Long,
    ): Notification {
        val notification =
            notificationRepository.findActiveById(notificationId)
                ?: throw NotificationNotFoundException()

        if (notification.userId != userId) throw InvalidNotificationAccessException()

        return notification
    }

    fun checkFirstReactionDuplicated(
        feedAuthorId: Long,
        feedId: Long,
        reactorId: Long,
    ): Boolean =
        notificationRepository.findByFeedIdAndUserIdInFirstReactions(
            feedAuthorId = feedAuthorId,
            notificationType = NotificationType.FEED_FIRST_REACTION,
            feedId = feedId,
            reactorId = reactorId,
        ) != null
}
