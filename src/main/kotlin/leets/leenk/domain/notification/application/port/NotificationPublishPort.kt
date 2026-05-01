package leets.leenk.domain.notification.application.port

import leets.leenk.domain.notification.domain.entity.Notification

interface NotificationPublishPort {
    suspend fun publish(
        userId: Long,
        notification: Notification,
    )
}
