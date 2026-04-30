package leets.leenk.domain.notification.domain.service

import leets.leenk.domain.notification.domain.entity.Notification
import leets.leenk.domain.notification.domain.entity.NotificationEntity
import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import leets.leenk.domain.notification.domain.repository.NotificationEntityRepository
import leets.leenk.domain.notification.domain.repository.NotificationRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.updateFirst
import org.springframework.stereotype.Service

@Service
class NotificationSaveService(
    private val notificationRepository: NotificationRepository,
    private val notificationEntityRepository: NotificationEntityRepository,
    private val mongoTemplate: MongoTemplate,
) {
    fun save(notification: Notification): Notification = notificationRepository.save(notification)

    fun save(notification: NotificationEntity): NotificationEntity = notificationEntityRepository.save(notification)

    /**
     * details 배열에 여러 항목을 한 번에 추가 (원자적 연산)
     * 단일 항목도 리스트로 감싸서 전달 가능
     */
    fun pushDetails(
        userId: Long,
        type: NotificationType,
        targetId: Long,
        details: List<Map<String, Any>>,
    ): NotificationEntity? {
        val query =
            Query.query(
                Criteria
                    .where("userId")
                    .`is`(userId)
                    .and("notificationType")
                    .`is`(type)
                    .and("content.targetId")
                    .`is`(targetId)
                    .and("deleteDate")
                    .`is`(null),
            )

        val update =
            Update().apply {
                push("content.metadata.details").each(*details.toTypedArray())
                set("isRead", false)

                // 최상단 title, body 업데이트
                getUpdatedTitleAndBody(type, details)?.let { (title, body) ->
                    set("content.title", title)
                    set("content.body", body)
                }
            }

        mongoTemplate.updateFirst<NotificationEntity>(query, update)
        return mongoTemplate.findOne<NotificationEntity>(query)
    }

    private fun getUpdatedTitleAndBody(
        type: NotificationType,
        details: List<Map<String, Any>>,
    ): Pair<String, String>? =
        when (type) {
            NotificationType.FEED_REACTION_COUNT -> {
                details.lastOrNull()?.let { lastDetail ->
                    val milestone = lastDetail["milestone"] as? Int
                    (lastDetail["title"] as? String ?: "Leenk") to
                        (lastDetail["body"] as? String ?: "내가 쓴 피드에 좋아요를 ${milestone}개 받았어")
                }
            }

            NotificationType.FEED_FIRST_REACTION -> {
                details.lastOrNull()?.let { lastDetail ->
                    (lastDetail["title"] as? String ?: "Leenk") to
                        (lastDetail["body"] as? String ?: "내가 쓴 피드에 좋아요를 받았어")
                }
            }

            NotificationType.NEW_LEENK_PARTICIPANT -> {
                details.lastOrNull()?.let { lastDetail ->
                    val participantName = lastDetail["participantName"] as? String ?: ""
                    (lastDetail["title"] as? String ?: "Leenk") to
                        NotificationType.NEW_LEENK_PARTICIPANT.formatContent(name = participantName)
                }
            }

            else -> {
                null
            }
        }
}
