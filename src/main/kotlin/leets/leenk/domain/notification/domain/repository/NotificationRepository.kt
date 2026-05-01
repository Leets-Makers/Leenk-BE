package leets.leenk.domain.notification.domain.repository

import leets.leenk.domain.notification.domain.entity.Notification
import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface NotificationRepository : MongoRepository<Notification, String> {
    @Query("{ 'userId': ?0, 'notificationType': ?1, 'content.targetId': ?2, 'deleteDate': null }")
    fun findNotificationByUserAndTypeAndTarget(
        userId: Long,
        notificationType: NotificationType,
        targetId: Long,
    ): Notification?

    fun findAllByUserId(userId: Long): List<Notification>

    @Query("{ 'userId': ?0, 'deleteDate': null }")
    fun findPageByUserIdAndDeleteDateIsNull(
        userId: Long,
        pageable: Pageable,
    ): Slice<Notification>

    @Query("{ 'userId': ?0, 'isRead': false, 'deleteDate': null }", count = true)
    fun countUnreadByUserId(userId: Long): Long

    @Query("{ '_id': ?0, 'deleteDate': null }")
    fun findActiveById(id: String): Notification?

    @Query(
        "{ 'userId': ?0, 'notificationType': ?1, 'content.targetId': ?2, 'content.metadata.details': { '\$elemMatch': { 'userId': ?3 } } }",
    )
    fun findByFeedIdAndUserIdInFirstReactions(
        feedAuthorId: Long,
        notificationType: NotificationType,
        feedId: Long,
        reactorId: Long,
    ): Notification?
}
