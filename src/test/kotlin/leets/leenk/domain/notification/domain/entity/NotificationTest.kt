package leets.leenk.domain.notification.domain.entity

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import leets.leenk.domain.notification.domain.entity.enums.NotificationType

class NotificationTest :
    DescribeSpec({

        describe("Notification") {
            context("markRead()") {
                it("알림을 읽음 상태로 변경하고 updateDate를 갱신해야 한다") {
                    val notification =
                        Notification(
                            id = "test-id",
                            userId = 1L,
                            notificationType = NotificationType.NEW_FEED,
                            content =
                                NotificationPayload(
                                    title = "Test",
                                    body = "Test body",
                                    path = "/feeds",
                                    targetId = 100L,
                                ),
                            isRead = false,
                        )
                    val beforeUpdate = notification.updateDate

                    Thread.sleep(10)
                    notification.markRead()

                    notification.isRead shouldBe true
                    notification.updateDate shouldNotBe beforeUpdate
                }
            }

            context("markUnread()") {
                it("알림을 읽지 않음 상태로 변경하고 updateDate를 갱신해야 한다") {
                    val notification =
                        Notification(
                            id = "test-id",
                            userId = 1L,
                            notificationType = NotificationType.NEW_FEED,
                            content =
                                NotificationPayload(
                                    title = "Test",
                                    body = "Test body",
                                    path = "/feeds",
                                    targetId = 100L,
                                ),
                            isRead = true,
                        )
                    val beforeUpdate = notification.updateDate

                    Thread.sleep(10)
                    notification.markUnread()

                    notification.isRead shouldBe false
                    notification.updateDate shouldNotBe beforeUpdate
                }
            }
        }
    })
