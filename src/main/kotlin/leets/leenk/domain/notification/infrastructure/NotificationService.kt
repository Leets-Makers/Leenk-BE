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
import org.springframework.dao.DuplicateKeyException
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

    private val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            log.error("알림 처리 중 예외 발생", throwable)
        }

    private val scope =
        CoroutineScope(
            SupervisorJob() + Dispatchers.IO + CoroutineName("notification") + exceptionHandler,
        )

    override fun send(request: NotificationRequest) {
        scope.launch {
            val notification = sendInternal(request)
            // 트랜잭션 밖에서 푸시 발행
            notification?.let { publishNotification(request.userId, it) }
        }
    }

    override fun sendBatch(requests: List<NotificationRequest>) {
        scope.launch {
            requests
                .map { request ->
                    async {
                        val notification = sendInternal(request)
                        // 트랜잭션 밖에서 푸시 발행
                        notification?.let { publishNotification(request.userId, it) }
                    }
                }.awaitAll()
        }
    }

    override fun sendOrUpdate(request: NotificationRequest) {
        scope.launch {
            val notification = sendOrUpdateInternal(request)
            // 트랜잭션 밖에서 푸시 발행
            notification?.let { publishNotification(request.userId, it) }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected open suspend fun sendOrUpdateInternal(request: NotificationRequest): NotificationEntity? {
        if (!notificationPolicy.shouldNotify(request.userId, request.type)) {
            return null
        }

        return try {
            val existing =
                notificationEntityGetService.findByUserIdAndTypeAndTargetId(
                    userId = request.userId,
                    type = request.type,
                    targetId = request.targetId,
                )

            if (existing != null) {
                // 기존 알림 있음 -> details 배열에 추가
                val details = request.metadata["details"] as? List<Map<String, Any>>
                if (details != null) {
                    notificationSaveService.pushDetails(
                        userId = request.userId,
                        type = request.type,
                        targetId = request.targetId,
                        details = details,
                    ) ?: existing
                } else {
                    existing
                }
            } else {
                // 새 알림 생성
                val new = createNotification(request)
                notificationSaveService.save(new)
                new
            }
        } catch (e: DuplicateKeyException) {
            // 경쟁 조건 발생: 다른 스레드가 먼저 생성함
            log.debug("알림 중복 생성 감지, push로 detail 추가: userId={}, type={}", request.userId, request.type, e)

            val details = request.metadata["details"] as? List<Map<String, Any>>
            details?.let {
                notificationSaveService.pushDetails(
                    userId = request.userId,
                    type = request.type,
                    targetId = request.targetId,
                    details = it,
                )
            }
        } catch (e: Exception) {
            log.error("알림 저장 중 예외 발생: userId={}, type={}", request.userId, request.type, e)
            null
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected open suspend fun sendInternal(request: NotificationRequest): NotificationEntity? {
        if (!notificationPolicy.shouldNotify(request.userId, request.type)) {
            return null
        }

        return try {
            val notification = createNotification(request)
            notificationSaveService.save(notification)
            notification
        } catch (e: Exception) {
            log.error("알림 저장 중 예외 발생: userId={}, type={}", request.userId, request.type, e)
            null
        }
    }

    private suspend fun publishNotification(
        userId: Long,
        notification: NotificationEntity,
    ) {
        try {
            notificationPublisher.publish(userId, notification)
        } catch (e: Exception) {
            log.warn("푸시 알림 발행 실패 (MongoDB 저장은 성공): userId={}", userId, e)
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
