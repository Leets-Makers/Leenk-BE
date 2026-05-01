package leets.leenk.domain.notification.domain.entity

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import java.time.LocalDateTime

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
                    val originalCreateDate = LocalDateTime.now().minusDays(1)
                    val notification =
                        Notification(
                            id = "test-id",
                            userId = 1L,
                            notificationType = NotificationType.FEED_FIRST_REACTION,
                            content =
                                NotificationPayload(
                                    title = "Old Title",
                                    body = "Old Body",
                                    path = "/feeds",
                                    targetId = 100L,
                                    metadata = mapOf("count" to 1),
                                ),
                            isRead = true,
                            createDate = originalCreateDate,
                        )

                    val newMetadata = mapOf("count" to 2, "users" to listOf("user1", "user2"))

                    val updated =
                        notification.updateContent(
                            newTitle = "New Title",
                            newBody = "New Body",
                            newMetadata = newMetadata,
                        )

                    updated.id shouldBe notification.id
                    updated.userId shouldBe notification.userId
                    updated.content.title shouldBe "New Title"
                    updated.content.body shouldBe "New Body"
                    updated.content.metadata shouldBe newMetadata
                    updated.isRead shouldBe false
                    updated.createDate shouldBe originalCreateDate
                    updated.updateDate shouldNotBe notification.updateDate
                }

                it("path와 targetId는 원본과 동일하게 유지되어야 한다") {
                    val notification =
                        Notification(
                            id = "test-id",
                            userId = 1L,
                            notificationType = NotificationType.FEED_FIRST_REACTION,
                            content =
                                NotificationPayload(
                                    title = "Old Title",
                                    body = "Old Body",
                                    path = "/feeds",
                                    targetId = 100L,
                                ),
                            isRead = false,
                        )

                    val updated =
                        notification.updateContent(
                            newTitle = "New Title",
                            newBody = "New Body",
                            newMetadata = emptyMap(),
                        )

                    updated.content.path shouldBe "/feeds"
                    updated.content.targetId shouldBe 100L
                }
            }
        }
    })
