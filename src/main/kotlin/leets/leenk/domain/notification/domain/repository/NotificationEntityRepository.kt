package leets.leenk.domain.notification.domain.repository

import leets.leenk.domain.notification.domain.entity.NotificationEntity
import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface NotificationEntityRepository : MongoRepository<NotificationEntity, String> {
    @Query("{ 'userId': ?0, 'notificationType': ?1, 'content.targetId': ?2 }")
    fun findNotificationByUserAndTypeAndTarget(
        userId: Long,
        notificationType: NotificationType,
        targetId: Long,
    ): NotificationEntity?

    fun findAllByUserId(userId: Long): List<NotificationEntity>

    /**
     * FEED_FIRST_REACTION 알림에서 특정 사용자의 공감이 이미 포함되어 있는지 확인
     * feedAuthorId의 알림 중에서 feedId에 대한 FEED_FIRST_REACTION 알림의 details에 reactorId가 있는지 확인
     */
    @Query(
        "{ 'userId': ?0, 'notificationType': ?1, 'content.targetId': ?2, 'content.metadata.details': { '\$elemMatch': { 'userId': ?3 } } }",
    )
    fun findByFeedIdAndUserIdInFirstReactions(
        feedAuthorId: Long,
        notificationType: NotificationType,
        feedId: Long,
        reactorId: Long,
    ): NotificationEntity?
}
