package leets.leenk.domain.notification.application.adapter

import leets.leenk.domain.feed.domain.event.FeedDomainEvent
import leets.leenk.domain.notification.application.dto.NotificationRequest
import leets.leenk.domain.notification.application.port.NotificationPort
import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import leets.leenk.domain.notification.domain.service.NotificationEntityGetService
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class FeedNotificationEventListener(
    private val notificationPort: NotificationPort,
    private val notificationEntityGetService: NotificationEntityGetService,
    private val userSettingGetService: leets.leenk.domain.user.domain.service.usersetting.UserSettingGetService,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onFeedCreated(event: FeedDomainEvent.Created) {
        handleFeedCreated(event)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onFeedReacted(event: FeedDomainEvent.Reacted) {
        handleFeedReacted(event)
    }

    private fun handleFeedCreated(event: FeedDomainEvent.Created) {
        sendNewFeedNotifications(event)

        if (event.taggedUserIds.isNotEmpty()) {
            sendFeedTagNotifications(event)
        }
    }

    private fun sendNewFeedNotifications(event: FeedDomainEvent.Created) {
        val usersToNotify = userSettingGetService.getUsersToNotifyNewFeed(event.authorId)

        if (usersToNotify.isEmpty()) {
            return
        }

        val newFeedRequests =
            usersToNotify.map { user ->
                NotificationRequest(
                    userId = user.id,
                    type = NotificationType.NEW_FEED,
                    targetId = event.feedId,
                )
            }

        notificationPort.sendBatch(newFeedRequests)
    }

    private fun sendFeedTagNotifications(event: FeedDomainEvent.Created) {
        val tagRequests =
            event.taggedUserIds
                .filter { it != event.authorId }
                .map { taggedUserId ->
                    NotificationRequest(
                        userId = taggedUserId,
                        type = NotificationType.FEED_TAG,
                        targetId = event.feedId,
                        name = event.authorName,
                    )
                }

        if (tagRequests.isNotEmpty()) {
            notificationPort.sendBatch(tagRequests)
        }
    }

    private fun handleFeedReacted(event: FeedDomainEvent.Reacted) {
        val now = java.time.LocalDateTime.now()

        val isFirstReactionDuplicated =
            notificationEntityGetService.checkFirstReactionDuplicated(
                feedAuthorId = event.feedAuthorId,
                feedId = event.feedId,
                reactorId = event.reactorId,
            )

        if (!isFirstReactionDuplicated) {
            val existingNotification =
                notificationEntityGetService.findByUserIdAndTypeAndTargetId(
                    userId = event.feedAuthorId,
                    type = NotificationType.FEED_FIRST_REACTION,
                    targetId = event.feedId,
                )

            val newDetail =
                mapOf(
                    "userId" to event.reactorId,
                    "name" to event.reactorName,
                    "title" to NotificationType.FEED_FIRST_REACTION.title,
                    "body" to NotificationType.FEED_FIRST_REACTION.formatContent(name = event.reactorName),
                    "createDate" to now,
                )

            if (existingNotification != null) {
                val currentDetails =
                    (existingNotification.content.metadata["details"] as? List<*>)?.filterIsInstance<Map<String, Any>>()
                        ?: emptyList()
                val updatedDetails = currentDetails + newDetail
                // TODO: 중첩 알림일 경우 1회만 체크하도록 수정
                notificationPort.sendOrUpdate(
                    NotificationRequest(
                        userId = event.feedAuthorId,
                        type = NotificationType.FEED_FIRST_REACTION,
                        targetId = event.feedId,
                        name = event.reactorName,
                        metadata = mapOf("details" to updatedDetails),
                    ),
                )
            } else {
                notificationPort.send(
                    NotificationRequest(
                        userId = event.feedAuthorId,
                        type = NotificationType.FEED_FIRST_REACTION,
                        targetId = event.feedId,
                        name = event.reactorName,
                        metadata = mapOf("details" to listOf(newDetail)),
                    ),
                )
            }
        }

        val achievedMilestones = findMilestonesBetween(event.previousReactionCount, event.totalReactionCount)

        if (achievedMilestones.isNotEmpty()) {
            var currentNotification =
                notificationEntityGetService.findByUserIdAndTypeAndTargetId(
                    userId = event.feedAuthorId,
                    type = NotificationType.FEED_REACTION_COUNT,
                    targetId = event.feedId,
                )

            val allDetails = mutableListOf<Map<String, Any>>()

            if (currentNotification != null) {
                val existingDetails =
                    (currentNotification.content.metadata["details"] as? List<*>)?.filterIsInstance<Map<String, Any>>()
                        ?: emptyList()
                allDetails.addAll(existingDetails)
            }

            achievedMilestones.forEach { milestone ->
                val alreadyExists =
                    allDetails.any { detail ->
                        (detail["milestone"] as? Int) == milestone
                    }

                if (!alreadyExists) {
                    allDetails.add(
                        mapOf(
                            "milestone" to milestone,
                            "title" to NotificationType.FEED_REACTION_COUNT.title,
                            "body" to NotificationType.FEED_REACTION_COUNT.formatContent(count = milestone),
                            "createDate" to now,
                        ),
                    )
                }
            }

            val lastMilestone = achievedMilestones.last()

            if (currentNotification != null) {
                notificationPort.sendOrUpdate(
                    NotificationRequest(
                        userId = event.feedAuthorId,
                        type = NotificationType.FEED_REACTION_COUNT,
                        targetId = event.feedId,
                        count = lastMilestone,
                        metadata =
                            mapOf(
                                "reactionCount" to lastMilestone,
                                "details" to allDetails,
                            ),
                    ),
                )
            } else {
                notificationPort.send(
                    NotificationRequest(
                        userId = event.feedAuthorId,
                        type = NotificationType.FEED_REACTION_COUNT,
                        targetId = event.feedId,
                        count = lastMilestone,
                        metadata =
                            mapOf(
                                "reactionCount" to lastMilestone,
                                "details" to allDetails,
                            ),
                    ),
                )
            }
        }
    }

    /**
     * 이전 카운트와 현재 카운트 사이에 달성한 마일스톤들을 찾는다
     * 예: previousCount=4, currentCount=26 → [5, 10, 25]
     */
    private fun findMilestonesBetween(
        previousCount: Long,
        currentCount: Long,
    ): List<Int> {
        val milestones = listOf(5, 10, 25, 50, 100, 250, 500, 1000, 2000, 5000, 10000, 20000, 50000, 100000)
        return milestones.filter { it > previousCount && it <= currentCount }
    }
}
