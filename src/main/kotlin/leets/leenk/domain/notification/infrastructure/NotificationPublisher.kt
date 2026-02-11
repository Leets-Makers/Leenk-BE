package leets.leenk.domain.notification.infrastructure

import leets.leenk.domain.notification.domain.entity.NotificationEntity
import leets.leenk.domain.user.domain.service.user.UserGetService
import leets.leenk.global.sqs.application.dto.SqsMessageEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class NotificationPublisher(
    private val eventPublisher: ApplicationEventPublisher,
    private val userGetService: UserGetService,
) {
    fun publish(
        userId: Long,
        notification: NotificationEntity,
    ) {
        val user = userGetService.findById(userId)
        val fcmToken = user.fcmToken ?: return

        val sqsEvent =
            SqsMessageEvent(
                notification.content.title,
                notification.content.body,
                fcmToken,
                notification.notificationType.path,
                user.id,
            )
        eventPublisher.publishEvent(sqsEvent)
    }
}
