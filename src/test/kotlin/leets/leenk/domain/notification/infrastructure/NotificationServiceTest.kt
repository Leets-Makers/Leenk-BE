package leets.leenk.domain.notification.infrastructure

import io.kotest.core.spec.style.DescribeSpec
import io.mockk.*
import leets.leenk.domain.notification.application.dto.NotificationRequest
import leets.leenk.domain.notification.application.policy.NotificationPolicy
import leets.leenk.domain.notification.domain.entity.NotificationEntity
import leets.leenk.domain.notification.domain.entity.NotificationPayload
import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import leets.leenk.domain.notification.domain.service.NotificationEntityGetService
import leets.leenk.domain.notification.domain.service.NotificationSaveService

class NotificationServiceTest :
    DescribeSpec({

        val notificationSaveService = mockk<NotificationSaveService>()
        val notificationEntityGetService = mockk<NotificationEntityGetService>()
        val notificationPublisher = mockk<NotificationPublisher>()
        val notificationPolicy = mockk<NotificationPolicy>()

        val notificationService =
            NotificationService(
                notificationSaveService,
                notificationEntityGetService,
                notificationPublisher,
                notificationPolicy,
            )

        beforeEach {
            clearAllMocks()
            every { notificationPolicy.shouldNotify(any(), any()) } returns true
            coEvery { notificationPublisher.publish(any(), any()) } just Runs
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
                    every { notificationSaveService.save(any<NotificationEntity>()) } answers { firstArg() }
                    notificationService.send(request)
                    Thread.sleep(100)
                    verify(exactly = 1) { notificationSaveService.save(any<NotificationEntity>()) }
                    coVerify(exactly = 1) { notificationPublisher.publish(1L, any()) }
                }
            }
            context("정책 조건을 만족하지 않는 경우") {
                it("알림을 저장하거나 발행하지 않아야 한다") {
                    val request = NotificationRequest(userId = 1L, type = NotificationType.NEW_FEED, targetId = 100L)
                    every { notificationPolicy.shouldNotify(1L, NotificationType.NEW_FEED) } returns false
                    notificationService.send(request)
                    Thread.sleep(100)
                    verify(exactly = 0) { notificationSaveService.save(any<NotificationEntity>()) }
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
                    every { notificationSaveService.save(any<NotificationEntity>()) } answers { firstArg() }
                    notificationService.sendBatch(requests)
                    Thread.sleep(200)
                    verify(exactly = 3) { notificationSaveService.save(any<NotificationEntity>()) }
                    coVerify(exactly = 3) { notificationPublisher.publish(any(), any()) }
                }
            }
            context("빈 리스트가 주어진 경우") {
                it("아무것도 처리하지 않아야 한다") {
                    notificationService.sendBatch(emptyList())
                    Thread.sleep(100)
                    verify(exactly = 0) { notificationSaveService.save(any<NotificationEntity>()) }
                }
            }
        }

        describe("sendOrUpdate()") {
            context("기존 알림이 존재하는 경우") {
                it("알림 내용을 업데이트하고 발행해야 한다") {
                    val existingNotification =
                        NotificationEntity(
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
                    val request =
                        NotificationRequest(
                            userId = 1L,
                            type = NotificationType.FEED_FIRST_REACTION,
                            targetId = 100L,
                            name = "새로운 사용자",
                            metadata =
                                mapOf("count" to 2),
                        )
                    every {
                        notificationEntityGetService.findByUserIdAndTypeAndTargetId(
                            1L,
                            NotificationType.FEED_FIRST_REACTION,
                            100L,
                        )
                    } returns existingNotification
                    every { notificationSaveService.save(any<NotificationEntity>()) } answers { firstArg() }
                    notificationService.sendOrUpdate(request)
                    Thread.sleep(100)
                    verify(exactly = 1) {
                        notificationSaveService.save(
                            match<NotificationEntity> {
                                it.id == "existing-id" &&
                                    it.content.title == "Leenk" &&
                                    it.isRead == false
                            },
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
                    every { notificationSaveService.save(any<NotificationEntity>()) } answers { firstArg() }
                    notificationService.sendOrUpdate(request)
                    Thread.sleep(100)
                    verify(exactly = 1) {
                        notificationSaveService.save(
                            match<NotificationEntity> {
                                it.userId == 1L &&
                                    it.notificationType == NotificationType.FEED_FIRST_REACTION &&
                                    it.content.targetId == 100L
                            },
                        )
                    }
                }
            }
        }

        afterSpec { notificationService.cleanup() }
    })
