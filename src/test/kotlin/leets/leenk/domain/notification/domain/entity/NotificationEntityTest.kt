package leets.leenk.domain.notification.domain.entity

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import leets.leenk.domain.notification.test.fixture.NotificationFixture

class NotificationEntityTest :
    DescribeSpec({

        describe("NotificationEntity") {
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

            context("updateContent()") {
                it("새로운 내용으로 알림을 업데이트하고 읽지 않음 상태로 설정해야 한다") {
                    val original = NotificationFixture.basicNotification(id = "test-id").copy(isRead = true)
                    val updated = original.copy(
                        content = original.content.copy(title = "New Title", body = "New Body"),
                        isRead = false,
                    )

                    updated.id shouldBe original.id
                    updated.userId shouldBe original.userId
                    updated.content.title shouldBe "New Title"
                    updated.content.body shouldBe "New Body"
                    updated.isRead shouldBe false
                }

                it("path와 targetId는 원본과 동일하게 유지되어야 한다") {
                    val original = NotificationFixture.basicNotification(id = "test-id")
                    val updated = original.copy(
                        content = original.content.copy(title = "New Title", body = "New Body"),
                    )

                    updated.content.path shouldBe original.content.path
                    updated.content.targetId shouldBe original.content.targetId
                }
            }
        }
    })
