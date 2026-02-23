package leets.leenk.domain.notification.infrastructure

import leets.leenk.domain.notification.application.port.NotificationPublishPort
import leets.leenk.domain.notification.domain.entity.NotificationEntity
import leets.leenk.domain.user.domain.service.user.UserGetService
import leets.leenk.global.sqs.application.dto.SqsMessageEvent
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class SqsNotificationPublisher(
    private val eventPublisher: ApplicationEventPublisher,
    private val userGetService: UserGetService,
) : NotificationPublishPort {
    private val log = LoggerFactory.getLogger(SqsNotificationPublisher::class.java)
    //TODO: 코틀린 의존성 추가 후 코틀린 형식으로 변경

    override suspend fun publish(
        userId: Long,
        notification: NotificationEntity,
    ) {
        try {
            val user = userGetService.findById(userId)
            val fcmToken =
                user.fcmToken ?: run {
                    log.debug("FCM 토큰을 찾을 수 없습니다: userId={}", userId)
                    return
                }

            val sqsEvent =
                SqsMessageEvent(
                    notification.content.title,
                    notification.content.body,
                    fcmToken,
                    notification.content.path,
                    user.id,
                )
            eventPublisher.publishEvent(sqsEvent)
            log.info("알림 발행 성공: userId={}, type={}", userId, notification.notificationType)
        } catch (e: Exception) {
            log.error("알림 발행 실패: userId={}, notificationId={}", userId, notification.id, e)
        }
    }
}
