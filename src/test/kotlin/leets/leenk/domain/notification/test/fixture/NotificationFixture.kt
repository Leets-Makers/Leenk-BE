package leets.leenk.domain.notification.test.fixture

import leets.leenk.domain.notification.domain.entity.Notification

object NotificationFixture {
    fun basicNotification(): Notification =
        Notification
            .builder()
            .id("100")
            .isRead(false)
            .userId(1L)
            .build()
}
