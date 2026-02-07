package leets.leenk.domain.feed.domain.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import leets.leenk.domain.feed.test.CommentTestFixture
import leets.leenk.domain.feed.test.FeedTestFixture
import leets.leenk.domain.feed.test.UserTestFixture
import java.time.LocalDateTime

class CommentDeleteServiceTest :
    BehaviorSpec({
        val commentDeleteService = CommentDeleteService()

        Given("사용자, 피드, 댓글이 주어졌을 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val feed = FeedTestFixture.createFeed(1L, user)
            val comment = CommentTestFixture.createComment(1L, user, feed, "hi")

            When("댓글을 삭제하면") {
                val start = LocalDateTime.now()
                commentDeleteService.deleteComment(comment)
                val end = LocalDateTime.now()

                Then("deletedAt이 현재 시간으로 설정되어야 한다") {
                    comment.deletedAt shouldNotBe null
                    (comment.deletedAt!! >= start) shouldBe true
                    (comment.deletedAt!! <= end) shouldBe true
                }
            }
        }
    })
