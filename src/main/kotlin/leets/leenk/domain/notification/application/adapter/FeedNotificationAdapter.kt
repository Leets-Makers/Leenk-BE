package leets.leenk.domain.notification.application.adapter

import leets.leenk.domain.feed.domain.event.FeedDomainEvent
import leets.leenk.domain.feed.domain.event.FeedEventType
import leets.leenk.domain.notification.application.dto.NotificationRequest
import leets.leenk.domain.notification.application.port.NotificationPort
import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import leets.leenk.domain.notification.domain.service.NotificationEntityGetService
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class FeedNotificationAdapter(
    private val notificationPort: NotificationPort,
    private val notificationEntityGetService: NotificationEntityGetService,
    private val userSettingGetService: leets.leenk.domain.user.domain.service.usersetting.UserSettingGetService,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun on(event: FeedDomainEvent) {
        when (event.eventType) {
            FeedEventType.CREATED -> handleFeedCreated(event)
            FeedEventType.REACTED -> handleFeedReacted(event)
        }
    }

    private fun handleFeedCreated(event: FeedDomainEvent) {
        sendNewFeedNotifications(event)

        if (event.taggedUserIds.isNotEmpty()) {
            sendTagNotifications(event)
        }
    }

    private fun sendNewFeedNotifications(event: FeedDomainEvent) {
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
                    dynamicParams = listOf(event.authorName),
                )
            }

        notificationPort.sendBatch(newFeedRequests)
    }

    private fun sendTagNotifications(event: FeedDomainEvent) {
        val tagRequests =
            event.taggedUserIds
                .filter { it != event.authorId }
                .map { taggedUserId ->
                    NotificationRequest(
                        userId = taggedUserId,
                        type = NotificationType.FEED_TAG,
                        targetId = event.feedId,
                        dynamicParams = listOf(event.authorName),
                    )
                }

        if (tagRequests.isNotEmpty()) {
            notificationPort.sendBatch(tagRequests)
        }
    }

    private fun handleFeedReacted(event: FeedDomainEvent) {
        val now = java.time.LocalDateTime.now()

        println("🎯 handleFeedReacted called - feedId: ${event.feedId}, totalReactionCount: ${event.totalReactionCount}")

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
                    "body" to NotificationType.FEED_FIRST_REACTION.formatContent(event.reactorName),
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
                        dynamicParams = listOf(event.reactorName),
                        metadata = mapOf("details" to updatedDetails),
                    ),
                )
            } else {
                notificationPort.send(
                    NotificationRequest(
                        userId = event.feedAuthorId,
                        type = NotificationType.FEED_FIRST_REACTION,
                        targetId = event.feedId,
                        dynamicParams = listOf(event.reactorName),
                        metadata = mapOf("details" to listOf(newDetail)),
                    ),
                )
            }
        }

        // 이벤트에서 이전 공감 수 가져오기
        val achievedMilestones = findMilestonesBetween(event.previousReactionCount, event.totalReactionCount)

        println("🎯 Milestone check - previousCount: ${event.previousReactionCount}, currentCount: ${event.totalReactionCount}, achievedMilestones: $achievedMilestones")

        // 여러 마일스톤을 순차적으로 처리 (하나의 알림에 집계)
        if (achievedMilestones.isNotEmpty()) {
            // 기존 알림 조회 (한 번만)
            var currentNotification =
                notificationEntityGetService.findByUserIdAndTypeAndTargetId(
                    userId = event.feedAuthorId,
                    type = NotificationType.FEED_REACTION_COUNT,
                    targetId = event.feedId,
                )

            val allDetails = mutableListOf<Map<String, Any>>()

            // 기존 details 가져오기
            if (currentNotification != null) {
                val existingDetails =
                    (currentNotification.content.metadata["details"] as? List<*>)?.filterIsInstance<Map<String, Any>>()
                        ?: emptyList()
                allDetails.addAll(existingDetails)
            }

            // 모든 마일스톤을 details에 추가
            achievedMilestones.forEach { milestone ->
                println("🎯 Processing milestone: $milestone")

                // 이미 존재하는 마일스톤인지 확인
                val alreadyExists = allDetails.any { detail ->
                    val body = detail["body"] as? String ?: ""
                    val numberRegex = """(\d+)개""".toRegex()
                    val match = numberRegex.find(body)
                    match?.groupValues?.get(1)?.toIntOrNull() == milestone
                }

                if (!alreadyExists) {
                    println("🎯 Adding milestone $milestone to details")
                    allDetails.add(
                        mapOf(
                            "title" to NotificationType.FEED_REACTION_COUNT.title,
                            "body" to NotificationType.FEED_REACTION_COUNT.formatContent(milestone),
                            "createDate" to now,
                        )
                    )
                } else {
                    println("🎯 Milestone $milestone already exists in details")
                }
            }

            // 마지막 마일스톤으로 알림 생성/업데이트
            val lastMilestone = achievedMilestones.last()

            if (currentNotification != null) {
                println("🎯 Updating existing notification with all milestones")
                notificationPort.sendOrUpdate(
                    NotificationRequest(
                        userId = event.feedAuthorId,
                        type = NotificationType.FEED_REACTION_COUNT,
                        targetId = event.feedId,
                        dynamicParams = listOf(lastMilestone),
                        metadata =
                            mapOf(
                                "reactionCount" to lastMilestone,
                                "details" to allDetails,
                            ),
                    ),
                )
            } else {
                println("🎯 Creating new notification with all milestones")
                notificationPort.send(
                    NotificationRequest(
                        userId = event.feedAuthorId,
                        type = NotificationType.FEED_REACTION_COUNT,
                        targetId = event.feedId,
                        dynamicParams = listOf(lastMilestone),
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
