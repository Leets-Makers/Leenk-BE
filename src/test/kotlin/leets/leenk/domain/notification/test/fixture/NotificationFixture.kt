package leets.leenk.domain.notification.test.fixture

import leets.leenk.domain.notification.domain.entity.NotificationEntity
import leets.leenk.domain.notification.domain.entity.NotificationPayload
import leets.leenk.domain.notification.domain.entity.enums.NotificationType

object NotificationFixture {
    fun basicNotification(
        id: String = "notification-1",
        userId: Long = 1L,
        notificationType: NotificationType = NotificationType.NEW_FEED,
    ): NotificationEntity =
        NotificationEntity(
            id = id,
            userId = userId,
            notificationType = notificationType,
            content =
                NotificationPayload(
                    title = "테스트 알림",
                    body = "테스트 알림 내용",
                    path = "feeds",
                    targetId = 1L,
                ),
        )

    fun notificationForUser(
        id: String = "notification-2",
        userId: Long,
        notificationType: NotificationType = NotificationType.NEW_FEED,
    ): NotificationEntity =
        NotificationEntity(
            id = id,
            userId = userId,
            notificationType = notificationType,
            content =
                NotificationPayload(
                    title = "테스트 알림",
                    body = "테스트 알림 내용",
                    path = "feeds",
                    targetId = 1L,
                ),
        )
}
