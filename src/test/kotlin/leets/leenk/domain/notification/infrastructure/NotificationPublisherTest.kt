package leets.leenk.domain.notification.infrastructure

import io.kotest.core.spec.style.DescribeSpec
import io.mockk.*
import leets.leenk.domain.notification.domain.entity.NotificationEntity
import leets.leenk.domain.notification.domain.entity.NotificationPayload
import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import leets.leenk.domain.user.domain.entity.User
import leets.leenk.domain.user.domain.service.user.UserGetService
import leets.leenk.global.sqs.application.dto.SqsMessageEvent
import org.springframework.context.ApplicationEventPublisher

class NotificationPublisherTest :
    DescribeSpec({

        val eventPublisher = mockk<ApplicationEventPublisher>()
        val userGetService = mockk<UserGetService>()

        val notificationPublisher =
            SqsNotificationPublisher(
                eventPublisher,
                userGetService,
            )

        beforeEach {
            clearAllMocks()
        }

        describe("publishIfEligible()") {
            context("FCM 토큰이 있는 경우") {
                it("SQS 이벤트를 발행해야 한다") {
                    // given
                    val userId = 1L
                    val fcmToken = "test-fcm-token"
                    val notification =
                        NotificationEntity(
                            id = "test-id",
                            userId = userId,
                            notificationType = NotificationType.NEW_FEED,
                            content =
                                NotificationPayload(
                                    title = "새 피드 알림",
                                    body = "홍길동님이 새 피드를 작성했습니다",
                                    path = "/feeds",
                                    targetId = 100L,
                                ),
                        )

                    val user = mockk<User>(relaxed = true)
                    every { user.id } returns userId
                    every { user.fcmToken } returns fcmToken

                    every { userGetService.findById(userId) } returns user
                    every { eventPublisher.publishEvent(any<SqsMessageEvent>()) } just Runs

                    // when
                    notificationPublisher.publish(userId, notification)

                    // then
                    verify(exactly = 1) {
                        eventPublisher.publishEvent(
                            match<SqsMessageEvent> {
                                it.title == notification.content.title &&
                                    it.content == notification.content.body &&
                                    it.fcmToken == fcmToken &&
                                    it.path == notification.notificationType.path &&
                                    it.id == userId
                            },
                        )
                    }
                }
            }

            context("FCM 토큰이 null인 경우") {
                it("이벤트를 발행하지 않아야 한다") {
                    // given
                    val userId = 1L
                    val notification =
                        NotificationEntity(
                            id = "test-id",
                            userId = userId,
                            notificationType = NotificationType.NEW_FEED,
                            content =
                                NotificationPayload(
                                    title = "Test",
                                    body = "Test body",
                                    path = "/feeds",
                                    targetId = 100L,
                                ),
                        )

                    val user = mockk<User>(relaxed = true)
                    every { user.id } returns userId
                    every { user.fcmToken } returns null

                    every { userGetService.findById(userId) } returns user

                    // when
                    notificationPublisher.publish(userId, notification)

                    // then
                    verify(exactly = 0) { eventPublisher.publishEvent(any<SqsMessageEvent>()) }
                }
            }
        }
    })
