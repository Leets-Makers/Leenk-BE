package leets.leenk.domain.notification.infrastructure

import jakarta.annotation.PreDestroy
import kotlinx.coroutines.*
import leets.leenk.domain.notification.application.dto.NotificationRequest
import leets.leenk.domain.notification.application.policy.NotificationPolicy
import leets.leenk.domain.notification.application.port.NotificationPort
import leets.leenk.domain.notification.domain.entity.NotificationEntity
import leets.leenk.domain.notification.domain.entity.NotificationPayload
import leets.leenk.domain.notification.domain.service.NotificationSaveService
import org.springframework.stereotype.Component

@Component
class NotificationService(
    private val notificationSaveService: NotificationSaveService,
    private val notificationPublisher: NotificationPublisher,
    private val notificationPolicy: NotificationPolicy
) : NotificationPort {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineName("notification")
    )

    override fun send(request: NotificationRequest) {
        scope.launch {
            sendInternal(request)
        }
    }

    override fun sendBatch(requests: List<NotificationRequest>) {
        scope.launch {
            requests.map { request ->
                async {
                    sendInternal(request)
                }
            }.awaitAll()
        }
    }

    private suspend fun sendInternal(request: NotificationRequest) {
        try {
            if (!notificationPolicy.shouldNotify(request.userId, request.type)) {
                return
            }

            val notification = createNotification(request)
            notificationSaveService.save(notification)
            notificationPublisher.publishIfEligible(request.userId, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createNotification(request: NotificationRequest): NotificationEntity {
        return NotificationEntity(
            userId = request.userId,
            notificationType = request.type,
            content = NotificationPayload(
                title = request.title,
                body = request.body,
                targetId = request.targetId,
                metadata = request.metadata
            ),
            isRead = false
        )
    }

    @PreDestroy
    fun cleanup() {
        scope.cancel()
    }
}
