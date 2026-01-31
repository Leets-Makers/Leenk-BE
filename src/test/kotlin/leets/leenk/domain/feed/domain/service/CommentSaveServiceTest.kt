package leets.leenk.domain.feed.domain.service

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.feed.domain.repository.CommentRepository
import leets.leenk.domain.feed.test.CommentTestFixture
import leets.leenk.domain.feed.test.FeedTestFixture
import leets.leenk.domain.feed.test.UserTestFixture

class CommentSaveServiceTest :
    BehaviorSpec({
        val commentRepository = mockk<CommentRepository>(relaxed = true)
        val commentSaveService = CommentSaveService(commentRepository)

        Given("사용자, 피드, 댓글이 주어졌을 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val feed = FeedTestFixture.createFeed(1L, user)
            val comment = CommentTestFixture.createComment(null, user, feed, "hi")

            every { commentRepository.save(any()) } returns comment

            When("댓글을 저장하면") {
                commentSaveService.saveComment(comment)

                Then("commentRepository의 save 메서드가 호출되어야 한다") {
                    verify { commentRepository.save(comment) }
                }
            }
        }
    })
