package leets.leenk.domain.notification.infrastructure

import leets.leenk.domain.notification.application.policy.NotificationPolicy
import leets.leenk.domain.notification.domain.entity.NotificationEntity
import leets.leenk.domain.user.domain.entity.User
import leets.leenk.domain.user.domain.service.user.UserGetService
import leets.leenk.global.sqs.application.dto.SqsMessageEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class NotificationPublisher(
    private val eventPublisher: ApplicationEventPublisher,
    private val notificationPolicy: NotificationPolicy,
    private val userGetService: UserGetService
) {
    fun publishIfEligible(userId: Long, notification: NotificationEntity) {
        if (!notificationPolicy.canPublishPush(userId)) {
            return
        }

        val user = userGetService.findById(userId)
        val fcmToken = user.getFcmTokenReflection() ?: return

        val sqsEvent = SqsMessageEvent(
            notification.content.title,
            notification.content.body,
            fcmToken,
            notification.notificationType.path,
            1L
        )
        eventPublisher.publishEvent(sqsEvent)
    }

    private fun User.getFcmTokenReflection(): String? {
        return try {
            val method = this.javaClass.getMethod("getFcmToken")
            method.invoke(this) as? String
        } catch (e: Exception) {
            null
        }
    }
}
