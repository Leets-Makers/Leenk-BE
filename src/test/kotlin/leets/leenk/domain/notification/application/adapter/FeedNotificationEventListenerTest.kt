package leets.leenk.domain.notification.application.adapter

import io.kotest.core.spec.style.DescribeSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import leets.leenk.domain.feed.domain.event.FeedDomainEventFixture
import leets.leenk.domain.notification.application.dto.NotificationRequest
import leets.leenk.domain.notification.application.port.NotificationPort
import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import leets.leenk.domain.notification.domain.service.NotificationEntityGetService
import leets.leenk.domain.user.domain.service.usersetting.UserSettingGetService

class FeedNotificationEventListenerTest :
    DescribeSpec({

        val notificationPort = mockk<NotificationPort>(relaxed = true)
        val notificationEntityGetService = mockk<NotificationEntityGetService>()
        val userSettingGetService = mockk<UserSettingGetService>()

        val listener =
            FeedNotificationEventListener(
                notificationPort,
                notificationEntityGetService,
                userSettingGetService,
            )

        beforeEach {
            clearAllMocks()
        }

        describe("onFeedCreated()") {
            context("NEW_FEED 알림이 켜진 유저가 존재하는 경우") {
                it("해당 유저들에게 NEW_FEED 알림을 sendBatch로 발송해야 한다") {
                    val subscribers =
                        listOf(
                            mockk<leets.leenk.domain.user.domain.entity.User>(relaxed = true).also {
                                every { it.id } returns 10L
                            },
                            mockk<leets.leenk.domain.user.domain.entity.User>(relaxed = true).also {
                                every { it.id } returns 11L
                            },
                        )
                    every { userSettingGetService.getUsersToNotifyNewFeed(1L) } returns subscribers

                    listener.onFeedCreated(FeedDomainEventFixture.created())

                    val slot = slot<List<NotificationRequest>>()
                    verify(exactly = 1) { notificationPort.sendBatch(capture(slot)) }
                    assert(slot.captured.size == 2)
                    assert(slot.captured.all { it.type == NotificationType.NEW_FEED && it.targetId == 100L })
                }
            }

            context("NEW_FEED 알림이 켜진 유저가 없는 경우") {
                it("sendBatch를 호출하지 않아야 한다") {
                    every { userSettingGetService.getUsersToNotifyNewFeed(1L) } returns emptyList()

                    listener.onFeedCreated(FeedDomainEventFixture.created())

                    verify(exactly = 0) { notificationPort.sendBatch(any()) }
                }
            }

            context("태그된 유저가 있는 경우") {
                it("작성자를 제외한 태그 유저에게 FEED_TAG 알림을 sendBatch로 발송해야 한다") {
                    every { userSettingGetService.getUsersToNotifyNewFeed(1L) } returns emptyList()

                    listener.onFeedCreated(FeedDomainEventFixture.created(taggedUserIds = listOf(1L, 2L, 3L)))

                    val slot = slot<List<NotificationRequest>>()
                    verify(exactly = 1) { notificationPort.sendBatch(capture(slot)) }
                    assert(slot.captured.size == 2) // 작성자 제외
                    assert(slot.captured.all { it.type == NotificationType.FEED_TAG })
                    assert(slot.captured.none { it.userId == 1L })
                }
            }

            context("태그된 유저가 없는 경우") {
                it("FEED_TAG 알림을 발송하지 않아야 한다") {
                    every { userSettingGetService.getUsersToNotifyNewFeed(1L) } returns emptyList()

                    listener.onFeedCreated(FeedDomainEventFixture.created())

                    verify(exactly = 0) { notificationPort.sendBatch(any()) }
                }
            }
        }

        describe("onFeedReacted()") {
            context("첫 번째 반응이고 중복이 아닌 경우") {
                it("FEED_FIRST_REACTION 알림을 sendOrUpdate로 발송해야 한다") {
                    every {
                        notificationEntityGetService.checkFirstReactionDuplicated(
                            feedAuthorId = 1L,
                            feedId = 100L,
                            reactorId = 2L,
                        )
                    } returns false

                    listener.onFeedReacted(FeedDomainEventFixture.reacted())

                    val slot = slot<NotificationRequest>()
                    verify(exactly = 1) { notificationPort.sendOrUpdate(capture(slot)) }
                    assert(slot.captured.type == NotificationType.FEED_FIRST_REACTION)
                    assert(slot.captured.userId == 1L)
                    assert(slot.captured.targetId == 100L)
                }
            }

            context("첫 번째 반응이 중복인 경우") {
                it("FEED_FIRST_REACTION 알림을 발송하지 않아야 한다") {
                    every {
                        notificationEntityGetService.checkFirstReactionDuplicated(
                            feedAuthorId = 1L,
                            feedId = 100L,
                            reactorId = 2L,
                        )
                    } returns true

                    listener.onFeedReacted(FeedDomainEventFixture.reacted())

                    verify(exactly = 0) { notificationPort.sendOrUpdate(any()) }
                }
            }

            context("마일스톤을 달성한 경우") {
                it("FEED_REACTION_COUNT 알림을 sendOrUpdateWithMultiplePush로 발송해야 한다") {
                    every {
                        notificationEntityGetService.checkFirstReactionDuplicated(any(), any(), any())
                    } returns true

                    listener.onFeedReacted(
                        FeedDomainEventFixture.reacted(previousReactionCount = 4L, totalReactionCount = 10L),
                    )

                    val slot = slot<NotificationRequest>()
                    verify(exactly = 1) { notificationPort.sendOrUpdateWithMultiplePush(capture(slot)) }
                    assert(slot.captured.type == NotificationType.FEED_REACTION_COUNT)
                    assert(slot.captured.count == 10L) // 최고 마일스톤
                    val details = slot.captured.metadata["details"] as List<*>
                    assert(details.size == 2) // 5, 10 두 개
                }
            }

            context("마일스톤을 달성하지 못한 경우") {
                it("FEED_REACTION_COUNT 알림을 발송하지 않아야 한다") {
                    every {
                        notificationEntityGetService.checkFirstReactionDuplicated(any(), any(), any())
                    } returns true

                    listener.onFeedReacted(
                        FeedDomainEventFixture.reacted(previousReactionCount = 1L, totalReactionCount = 4L),
                    )

                    verify(exactly = 0) { notificationPort.sendOrUpdateWithMultiplePush(any()) }
                }
            }

            context("마일스톤 경계값 테스트") {
                it("previousCount=4, currentCount=5 이면 마일스톤 [5] 하나만 달성해야 한다") {
                    every {
                        notificationEntityGetService.checkFirstReactionDuplicated(any(), any(), any())
                    } returns true

                    listener.onFeedReacted(
                        FeedDomainEventFixture.reacted(previousReactionCount = 4L, totalReactionCount = 5L),
                    )

                    val slot = slot<NotificationRequest>()
                    verify(exactly = 1) { notificationPort.sendOrUpdateWithMultiplePush(capture(slot)) }
                    val details = slot.captured.metadata["details"] as List<*>
                    assert(details.size == 1)
                    assert(slot.captured.count == 5L)
                }

                it("previousCount=5, currentCount=5 이면 마일스톤이 없어야 한다") {
                    every {
                        notificationEntityGetService.checkFirstReactionDuplicated(any(), any(), any())
                    } returns true

                    listener.onFeedReacted(
                        FeedDomainEventFixture.reacted(previousReactionCount = 5L, totalReactionCount = 5L),
                    )

                    verify(exactly = 0) { notificationPort.sendOrUpdateWithMultiplePush(any()) }
                }
            }
        }
    })
