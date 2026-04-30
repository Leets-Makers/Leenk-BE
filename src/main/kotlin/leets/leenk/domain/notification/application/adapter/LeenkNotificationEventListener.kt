package leets.leenk.domain.notification.application.adapter

import leets.leenk.domain.leenk.domain.event.LeenkDomainEvent
import leets.leenk.domain.notification.application.dto.NotificationRequest
import leets.leenk.domain.notification.application.port.NotificationPort
import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import leets.leenk.domain.user.domain.service.usersetting.UserSettingGetService
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.time.LocalDateTime

@Component
class LeenkNotificationEventListener(
    private val notificationPort: NotificationPort,
    private val userSettingGetService: UserSettingGetService,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onNewLeenk(event: LeenkDomainEvent.NewLeenk) {
        val usersToNotify = userSettingGetService.getUsersToNotifyNewLeenk(event.hostId)

        if (usersToNotify.isEmpty()) return

        val requests =
            usersToNotify.map { user ->
                NotificationRequest(
                    userId = user.id,
                    type = NotificationType.NEW_LEENK,
                    targetId = event.leenkId,
                    name = event.hostName,
                    title = event.leenkTitle,
                    metadata =
                        mapOf(
                            "authorUserId" to event.hostId,
                        ),
                )
            }
        notificationPort.sendBatch(requests)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onParticipantJoined(event: LeenkDomainEvent.ParticipantJoined) {
        // 새로 참여한 사람에게 '참여 완료' 알림
        notificationPort.send(
            NotificationRequest(
                userId = event.newParticipantId,
                type = NotificationType.LEENK_JOIN_COMPLETED,
                targetId = event.leenkId,
                title = event.leenkTitle,
            ),
        )

        if (event.existingParticipantIds.isEmpty()) return

        val now = LocalDateTime.now()
        val participantDetail =
            mapOf(
                "participantId" to event.newParticipantId,
                "participantName" to event.newParticipantName,
                "createDate" to now,
            )

        // 기존 참여자들에게 새 참여자 알림 (집계: sendOrUpdate가 기존 알림 있으면 details에 추가, 없으면 새로 생성)
        event.existingParticipantIds.forEach { participantId ->
            notificationPort.sendOrUpdate(
                NotificationRequest(
                    userId = participantId,
                    type = NotificationType.NEW_LEENK_PARTICIPANT,
                    targetId = event.leenkId,
                    name = event.newParticipantName,
                    title = event.leenkTitle,
                    metadata = mapOf("details" to listOf(participantDetail)),
                ),
            )
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onParticipantKicked(event: LeenkDomainEvent.ParticipantKicked) {
        notificationPort.send(
            NotificationRequest(
                userId = event.kickedUserId,
                type = NotificationType.KICKED_FROM_LEENK,
                targetId = event.leenkId,
                title = event.leenkTitle,
            ),
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onParticipantLeft(event: LeenkDomainEvent.ParticipantLeft) {
        notificationPort.send(
            NotificationRequest(
                userId = event.hostId,
                type = NotificationType.LEENK_LEFT,
                targetId = event.leenkId,
                name = event.leftUserName,
                title = event.leenkTitle,
                metadata =
                    mapOf(
                        "leftUserId" to event.leftUserId,
                    ),
            ),
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onClosed(event: LeenkDomainEvent.Closed) {
        sendBatchToParticipants(
            participantIds = event.participantIds,
            type = NotificationType.LEENK_CLOSED,
            leenkId = event.leenkId,
            leenkTitle = event.leenkTitle,
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onStartingSoon(event: LeenkDomainEvent.StartingSoon) {
        if (event.participantIds.isEmpty()) return

        val metadata =
            buildMap<String, Any> {
                event.placeId?.let { put("placeId", it) }
                event.placeName?.let { put("placeName", it) }
                event.startTime?.let { put("startTime", it.toString()) }
            }

        val requests =
            event.participantIds.map { userId ->
                NotificationRequest(
                    userId = userId,
                    type = NotificationType.LEENK_STARTING_SOON,
                    targetId = event.leenkId,
                    title = event.leenkTitle,
                    metadata = metadata,
                )
            }
        notificationPort.sendBatch(requests)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onFinished(event: LeenkDomainEvent.Finished) {
        sendBatchToParticipants(
            participantIds = event.participantIds,
            type = NotificationType.LEENK_FINISHED,
            leenkId = event.leenkId,
            leenkTitle = event.leenkTitle,
        )
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onHostReminder(event: LeenkDomainEvent.HostReminder) {
        notificationPort.send(
            NotificationRequest(
                userId = event.hostId,
                type = NotificationType.LEENK_STARTED_HOST_REMINDER,
                targetId = event.leenkId,
                title = event.leenkTitle,
            ),
        )
    }

    private fun sendBatchToParticipants(
        participantIds: List<Long>,
        type: NotificationType,
        leenkId: Long,
        leenkTitle: String,
    ) {
        if (participantIds.isEmpty()) return

        val requests =
            participantIds.map { userId ->
                NotificationRequest(
                    userId = userId,
                    type = type,
                    targetId = leenkId,
                    title = leenkTitle,
                )
            }
        notificationPort.sendBatch(requests)
    }
}
