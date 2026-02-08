package leets.leenk.domain.notification.application.adapter

import leets.leenk.domain.feed.domain.event.FeedDomainEvent
import leets.leenk.domain.feed.domain.event.FeedEventType
import leets.leenk.domain.notification.application.dto.NotificationRequest
import leets.leenk.domain.notification.application.port.NotificationPort
import leets.leenk.domain.notification.domain.enums.NotificationType
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class FeedNotificationAdapter(
    private val notificationPort: NotificationPort
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun on(event: FeedDomainEvent) {
        when (event.eventType) {
            FeedEventType.REACTED -> handleFeedReacted(event)
            else -> {}
        }
    }

    private fun handleFeedReacted(event: FeedDomainEvent) {
        notificationPort.send(
            NotificationRequest(
                userId = event.feedAuthorId,
                type = NotificationType.FEED_FIRST_REACTION,
                title = NotificationType.FEED_FIRST_REACTION.title,
                body = NotificationType.FEED_FIRST_REACTION.content,
                targetId = event.feedId,
                metadata = mapOf(
                    "reactorId" to event.reactorId,
                    "reactorName" to event.reactorName
                )
            )
        )

        val milestone = findMilestone(event.totalReactionCount)
        if (milestone != null) {
            notificationPort.send(
                NotificationRequest(
                    userId = event.feedAuthorId,
                    type = NotificationType.FEED_REACTION_COUNT,
                    title = NotificationType.FEED_REACTION_COUNT.title,
                    body = NotificationType.FEED_REACTION_COUNT.formatContent(
                        mapOf("count" to milestone)
                    ),
                    targetId = event.feedId,
                    metadata = mapOf("reactionCount" to milestone)
                )
            )
        }
    }

    private fun findMilestone(count: Int): Int? {
        val milestones = listOf(5, 10, 25, 50, 100, 250, 500, 1000)
        return milestones.find { it == count }
    }
}
