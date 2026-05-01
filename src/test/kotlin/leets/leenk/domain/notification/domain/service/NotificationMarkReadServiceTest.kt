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
import leets.leenk.domain.notification.application.mapper.NotificationResponseMapper
import leets.leenk.domain.notification.application.usecase.NotificationUseCase
import leets.leenk.domain.notification.domain.repository.NotificationRepository
import leets.leenk.domain.notification.test.fixture.NotificationFixture
import leets.leenk.domain.user.test.fixture.UserTestFixture

class NotificationMarkReadServiceTest :
    StringSpec({
        val notificationRepository = mockk<NotificationRepository>()
        val notificationGetService = NotificationGetService(notificationRepository)
        val notificationSaveService = mockk<NotificationSaveService>()
        val notificationResponseMapper = mockk<NotificationResponseMapper>()
        val notificationUseCase =
            NotificationUseCase(notificationGetService, notificationSaveService, notificationResponseMapper)

        beforeEach {
            clearMocks(notificationRepository, notificationSaveService)
        }

        "유효한 알림 ID로 읽음 처리 요청 시 알림의 읽음 상태가 true로 변경되어야 한다" {
            val user = UserTestFixture.createUser(id = 1L)
            val notification = NotificationFixture.basicNotification(userId = 1L)

            every { notificationRepository.findActiveById(notification.id!!) } returns notification
            every { notificationSaveService.save(notification) } returns notification

            notificationUseCase.markAsRead(user.id, notification.id!!)

            notification.isRead shouldBe true
        }

        "존재하지 않는 알림 ID로 읽음 처리 요청 시 NotificationNotFoundException이 발생해야 한다" {
            val user = UserTestFixture.createUser(id = 1L)
            val invalidNotificationId = "999"

            every { notificationRepository.findActiveById(invalidNotificationId) } returns null

            shouldThrow<NotificationNotFoundException> {
                notificationUseCase.markAsRead(user.id, invalidNotificationId)
            }

            verify(exactly = 0) { notificationSaveService.save(any()) }
        }

        "다른 사용자의 알림에 대한 읽음 처리 요청 시 InvalidNotificationAccessException이 발생해야 한다" {
            val user = UserTestFixture.createUser(id = 1L)
            val otherUsersNotification = NotificationFixture.notificationForUser(userId = 2L)

            every { notificationRepository.findActiveById(otherUsersNotification.id!!) } returns
                otherUsersNotification

            shouldThrow<InvalidNotificationAccessException> {
                notificationUseCase.markAsRead(user.id, otherUsersNotification.id!!)
            }

            otherUsersNotification.isRead shouldBe false
            verify(exactly = 0) { notificationSaveService.save(any()) }
        }
    })
