package leets.leenk.domain.notification.domain.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.notification.domain.repository.NotificationRepository
import leets.leenk.domain.notification.test.fixture.NotificationFixture
import leets.leenk.domain.user.test.fixture.UserFixture
import java.util.Optional

class NotificationMarkReadServiceTest :
    StringSpec({
        val notificationRepository = mockk<NotificationRepository>()
        val notificationMarkReadService = NotificationMarkReadService(notificationRepository)

        "유효한 알림 ID로 읽음 처리 요청 시 알림의 읽음 상태가 true로 변경되어야 한다" {
            val user = UserFixture.basicUser()
            val notification = NotificationFixture.basicNotification()

            every { notificationRepository.findById(notification.id) } returns Optional.of(notification)
            every { notificationRepository.save(notification) } returns notification

            notificationMarkReadService.markReadNotification(user, notification.id)

            notification.isRead shouldBe true
            verify(exactly = 1) { notificationRepository.save(notification) }
        }
    })
