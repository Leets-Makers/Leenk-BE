package leets.leenk.domain.notification.application.port

import leets.leenk.domain.notification.domain.entity.NotificationEntity

interface NotificationPublishPort {
    suspend fun publish(userId: Long, notification: NotificationEntity)
}
