package leets.leenk.domain.notification.test.fixture

import leets.leenk.domain.notification.domain.entity.Notification

object NotificationFixture {
    // TODO: Notification을 Kotlin으로 마이그레이션한 후 Named Arguments로 변경
    fun basicNotification(

    ): Notification {
        return Notification.builder()
            .id("100")
            .isRead(false)
            .userId(1L)
            .build();
    }
}
