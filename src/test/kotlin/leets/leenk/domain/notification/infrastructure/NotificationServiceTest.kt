package leets.leenk.domain.notification.infrastructure

import io.kotest.core.spec.style.DescribeSpec
import io.mockk.*
import kotlinx.coroutines.delay
import leets.leenk.domain.notification.application.dto.NotificationRequest
import leets.leenk.domain.notification.application.policy.NotificationPolicy
import leets.leenk.domain.notification.domain.entity.Notification
import leets.leenk.domain.notification.domain.entity.NotificationPayload
import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import leets.leenk.domain.notification.domain.service.NotificationEntityGetService
import leets.leenk.domain.notification.domain.service.NotificationSaveService

class NotificationServiceTest :
    DescribeSpec({

        val notificationSaveService = mockk<NotificationSaveService>()
        val notificationEntityGetService = mockk<NotificationEntityGetService>()
        val notificationPublisher = mockk<SqsNotificationPublisher>()
        val notificationPolicy = mockk<NotificationPolicy>()

        lateinit var notificationService: NotificationService

        beforeEach {
            clearAllMocks()
            every { notificationPolicy.shouldNotify(any(), any()) } returns true
            coEvery { notificationPublisher.publish(any(), any()) } just Runs

            notificationService =
                spyk(
                    NotificationService(
                        notificationSaveService,
                        notificationEntityGetService,
                        notificationPublisher,
                        notificationPolicy,
                    ),
                )
        }

        describe("send()") {
            context("정책 조건을 만족하는 경우") {
                it("알림을 저장하고 발행해야 한다") {
                    val request =
                        NotificationRequest(
                            userId = 1L,
                            type = NotificationType.NEW_FEED,
                            targetId = 100L,
                        )
                    every { notificationSaveService.save(any<Notification>()) } answers { firstArg() }
                    notificationService.send(request)
                    delay(200)
                    verify(exactly = 1) { notificationSaveService.save(any<Notification>()) }
                    coVerify(exactly = 1) { notificationPublisher.publish(1L, any()) }
                }
            }
            context("정책 조건을 만족하지 않는 경우") {
                it("알림을 저장하거나 발행하지 않아야 한다") {
                    val request = NotificationRequest(userId = 1L, type = NotificationType.NEW_FEED, targetId = 100L)
                    every { notificationPolicy.shouldNotify(1L, NotificationType.NEW_FEED) } returns false
                    notificationService.send(request)
                    delay(200)
                    verify(exactly = 0) { notificationSaveService.save(any<Notification>()) }
                    coVerify(exactly = 0) { notificationPublisher.publish(any(), any()) }
                }
            }
        }

        describe("sendBatch()") {
            context("여러 알림 요청이 주어진 경우") {
                it("모든 알림을 병렬로 저장하고 발행해야 한다") {
                    val requests =
                        listOf(
                            NotificationRequest(userId = 1L, type = NotificationType.NEW_FEED, targetId = 100L),
                            NotificationRequest(userId = 2L, type = NotificationType.FEED_TAG, targetId = 101L),
                            NotificationRequest(userId = 3L, type = NotificationType.NEW_LEENK, targetId = 102L),
                        )
                    every { notificationSaveService.save(any<Notification>()) } answers { firstArg() }
                    notificationService.sendBatch(requests)
                    delay(300)
                    verify(exactly = 3) { notificationSaveService.save(any<Notification>()) }
                    coVerify(exactly = 3) { notificationPublisher.publish(any(), any()) }
                }
            }
            context("빈 리스트가 주어진 경우") {
                it("아무것도 처리하지 않아야 한다") {
                    notificationService.sendBatch(emptyList())
                    delay(200)
                    verify(exactly = 0) { notificationSaveService.save(any<Notification>()) }
                }
            }
        }

        describe("sendOrUpdate()") {
            context("기존 알림이 존재하고 details가 있는 경우") {
                it("details를 업데이트하고 발행해야 한다") {
                    val existingNotification =
                        Notification(
                            id = "existing-id",
                            userId = 1L,
                            notificationType = NotificationType.FEED_FIRST_REACTION,
                            content =
                                NotificationPayload(
                                    title = "Old Title",
                                    body = "Old Body",
                                    path = "/feeds",
                                    targetId = 100L,
                                    metadata =
                                        mapOf("count" to 1),
                                ),
                            isRead = false,
                        )
                    val details =
                        listOf(
                            mapOf("title" to "New Title", "body" to "New Body"),
                        )
                    val request =
                        NotificationRequest(
                            userId = 1L,
                            type = NotificationType.FEED_FIRST_REACTION,
                            targetId = 100L,
                            name = "새로운 사용자",
                            metadata =
                                mapOf("details" to details),
                        )
                    every {
                        notificationEntityGetService.findByUserIdAndTypeAndTargetId(
                            1L,
                            NotificationType.FEED_FIRST_REACTION,
                            100L,
                        )
                    } returns existingNotification
                    every {
                        notificationSaveService.pushDetails(
                            userId = 1L,
                            type = NotificationType.FEED_FIRST_REACTION,
                            targetId = 100L,
                            details = details,
                        )
                    } returns existingNotification
                    notificationService.sendOrUpdate(request)
                    delay(200)
                    verify(exactly = 1) {
                        notificationSaveService.pushDetails(
                            userId = 1L,
                            type = NotificationType.FEED_FIRST_REACTION,
                            targetId = 100L,
                            details = details,
                        )
                    }
                    coVerify(exactly = 1) { notificationPublisher.publish(1L, any()) }
                }
            }
            context("기존 알림이 존재하지 않는 경우") {
                it("새로운 알림을 생성하고 발행해야 한다") {
                    val request =
                        NotificationRequest(
                            userId = 1L,
                            type = NotificationType.FEED_FIRST_REACTION,
                            targetId = 100L,
                            name = "홍길동",
                        )
                    every {
                        notificationEntityGetService.findByUserIdAndTypeAndTargetId(
                            1L,
                            NotificationType.FEED_FIRST_REACTION,
                            100L,
                        )
                    } returns null
                    every { notificationSaveService.save(any<Notification>()) } answers { firstArg() }
                    notificationService.sendOrUpdate(request)
                    delay(200)
                    verify(exactly = 1) {
                        notificationSaveService.save(
                            match<Notification> {
                                it.userId == 1L &&
                                    it.notificationType == NotificationType.FEED_FIRST_REACTION &&
                                    it.content.targetId == 100L
                            },
                        )
                    }
                }
            }
            context("정책 조건을 만족하지 않는 경우") {
                it("알림을 저장하거나 발행하지 않아야 한다") {
                    val request =
                        NotificationRequest(
                            userId = 1L,
                            type = NotificationType.FEED_FIRST_REACTION,
                            targetId = 100L,
                        )
                    every { notificationPolicy.shouldNotify(1L, NotificationType.FEED_FIRST_REACTION) } returns false
                    notificationService.sendOrUpdate(request)
                    delay(200)
                    verify(exactly = 0) { notificationSaveService.save(any<Notification>()) }
                    verify(exactly = 0) { notificationSaveService.pushDetails(any(), any(), any(), any()) }
                    coVerify(exactly = 0) { notificationPublisher.publish(any(), any()) }
                }
            }
            context("알림 발행 중 예외가 발생한 경우") {
                it("알림은 저장되고 예외는 전파되지 않아야 한다") {
                    val request =
                        NotificationRequest(
                            userId = 1L,
                            type = NotificationType.FEED_FIRST_REACTION,
                            targetId = 100L,
                            name = "홍길동",
                        )
                    every {
                        notificationEntityGetService.findByUserIdAndTypeAndTargetId(
                            1L,
                            NotificationType.FEED_FIRST_REACTION,
                            100L,
                        )
                    } returns null
                    every { notificationSaveService.save(any<Notification>()) } answers { firstArg() }
                    coEvery { notificationPublisher.publish(any(), any()) } throws RuntimeException("발행 실패")
                    notificationService.sendOrUpdate(request)
                    delay(200)
                    verify(exactly = 1) { notificationSaveService.save(any<Notification>()) }
                    coVerify(exactly = 1) { notificationPublisher.publish(1L, any()) }
                }
            }
        }

        afterSpec { notificationService.cleanup() }
    })
