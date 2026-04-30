package leets.leenk.domain.notification.domain.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.notification.application.exception.InvalidNotificationAccessException
import leets.leenk.domain.notification.application.exception.NotificationNotFoundException
import leets.leenk.domain.user.test.fixture.UserFixture
import java.util.Optional

class NotificationMarkReadServiceTest :
    StringSpec({
        val notificationRepository = mockk<NotificationRepository>()
        val notificationMarkReadService = NotificationMarkReadService(notificationRepository)

        beforeEach {
            clearMocks(notificationRepository)
        }

        "유효한 알림 ID로 읽음 처리 요청 시 알림의 읽음 상태가 true로 변경되어야 한다" {
            val user = UserFixture.basicUser()
            val notification = NotificationFixture.basicNotification()

            every { notificationRepository.findById(notification.id) } returns Optional.of(notification)
            every { notificationRepository.save(notification) } returns notification

            notificationMarkReadService.markReadNotification(user, notification.id)

            notification.isRead shouldBe true
        }

        "존재하지 않는 알림 ID로 읽음 처리 요청 시 NotificationNotFoundException이 발생해야 한다" {
            val user = UserFixture.basicUser()
            val invalidNotificationId = "999"

            every { notificationRepository.findById(invalidNotificationId) } returns Optional.empty()

            shouldThrow<NotificationNotFoundException> {
                notificationMarkReadService.markReadNotification(user, invalidNotificationId)
            }

            verify(exactly = 0) { notificationRepository.save(any()) }
        }

        "다른 사용자의 알림에 대한 읽음 처리 요청 시 InvalidNotificationAccessException이 발생해야 한다" {
            val user = UserFixture.basicUser() // userId = 1L
            val otherUsersNotification = NotificationFixture.notificationForUser(userId = 2L)

            every { notificationRepository.findById(otherUsersNotification.id) } returns
                Optional.of(otherUsersNotification)

            shouldThrow<InvalidNotificationAccessException> {
                notificationMarkReadService.markReadNotification(user, otherUsersNotification.id)
            }

            otherUsersNotification.isRead shouldBe false
            verify(exactly = 0) { notificationRepository.save(any()) }
        }
    })
