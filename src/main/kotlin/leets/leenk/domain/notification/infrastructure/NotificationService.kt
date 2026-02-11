package leets.leenk.domain.notification.infrastructure

import jakarta.annotation.PreDestroy
import kotlinx.coroutines.*
import leets.leenk.domain.notification.application.dto.NotificationRequest
import leets.leenk.domain.notification.application.policy.NotificationPolicy
import leets.leenk.domain.notification.application.port.NotificationPort
import leets.leenk.domain.notification.domain.entity.NotificationEntity
import leets.leenk.domain.notification.domain.entity.NotificationPayload
import leets.leenk.domain.notification.domain.service.NotificationEntityGetService
import leets.leenk.domain.notification.domain.service.NotificationSaveService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class NotificationService(
    private val notificationSaveService: NotificationSaveService,
    private val notificationEntityGetService: NotificationEntityGetService,
    private val notificationPublisher: NotificationPublisher,
    private val notificationPolicy: NotificationPolicy,
) : NotificationPort {
    private val log = LoggerFactory.getLogger(javaClass)
    private val scope =
        CoroutineScope(
            SupervisorJob() + Dispatchers.IO + CoroutineName("notification"),
        )

    override fun send(request: NotificationRequest) {
        scope.launch {
            sendInternal(request)
        }
    }

    override fun sendBatch(requests: List<NotificationRequest>) {
        scope.launch {
            requests
                .map { request ->
                    async {
                        sendInternal(request)
                    }
                }.awaitAll()
        }
    }

    override fun sendOrUpdate(request: NotificationRequest) {
        scope.launch {
            sendOrUpdateInternal(request)
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected open suspend fun sendOrUpdateInternal(request: NotificationRequest) {
        if (!notificationPolicy.shouldNotify(request.userId, request.type)) {
            return
        }

        val existing =
            notificationEntityGetService.findByUserIdAndTypeAndTargetId(
                userId = request.userId,
                type = request.type,
                targetId = request.targetId,
            )

        val notification =
            if (existing != null) {
                val updated =
                    existing.updateContent(
                        newTitle = request.notificationTitle,
                        newBody = request.body,
                        newMetadata = request.metadata,
                    )
                notificationSaveService.save(updated)
                updated
            } else {
                val new = createNotification(request)
                notificationSaveService.save(new)
                new
            }

        // 트랜잭션 밖에서 푸시 발행
        try {
            notificationPublisher.publish(request.userId, notification)
        } catch (e: Exception) {
            log.warn("푸시 알림 발행 실패 (MongoDB 저장은 성공): userId={}, type={}", request.userId, request.type, e)
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected open suspend fun sendInternal(request: NotificationRequest) {
        if (!notificationPolicy.shouldNotify(request.userId, request.type)) {
            return
        }

        val notification = createNotification(request)
        notificationSaveService.save(notification)

        // 트랜잭션 밖에서 푸시 발행
        try {
            notificationPublisher.publish(request.userId, notification)
        } catch (e: Exception) {
            log.warn("푸시 알림 발행 실패 (MongoDB 저장은 성공): userId={}, type={}", request.userId, request.type, e)
        }
    }

    private fun createNotification(request: NotificationRequest): NotificationEntity =
        NotificationEntity(
            userId = request.userId,
            notificationType = request.type,
            content =
                NotificationPayload(
                    title = request.notificationTitle,
                    body = request.body,
                    path = request.path,
                    targetId = request.targetId,
                    metadata = request.metadata,
                ),
            isRead = false,
        )

    @PreDestroy
    fun cleanup() {
        scope.cancel()
    }
}
