package leets.leenk.domain.feed.domain.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.feed.application.exception.CommentNotFoundException
import leets.leenk.domain.feed.domain.repository.CommentRepository
import leets.leenk.domain.feed.test.CommentTestFixture
import leets.leenk.domain.feed.test.FeedTestFixture
import leets.leenk.domain.feed.test.UserTestFixture
import java.util.Optional

class CommentGetServiceTest :
    BehaviorSpec({
        val commentRepository = mockk<CommentRepository>()
        val commentGetService = CommentGetService(commentRepository)

        Given("삭제되지 않은 댓글이 존재할 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val feed = FeedTestFixture.createFeed(1L, user)
            val comment = CommentTestFixture.createComment(12L, user, feed, "hi")

            every { commentRepository.findByCommentIdAndDeletedAtIsNull(12L) } returns Optional.of(comment)

            When("ID로 댓글을 조회하면") {
                val result = commentGetService.findCommentByIdNotDeleted(12L)

                Then("해당 댓글이 반환되어야 한다") {
                    result shouldBe comment
                    verify { commentRepository.findByCommentIdAndDeletedAtIsNull(12L) }
                }
            }
        }

        Given("삭제되지 않은 댓글이 존재하지 않을 때") {
            every { commentRepository.findByCommentIdAndDeletedAtIsNull(99L) } returns Optional.empty()

            When("ID로 댓글을 조회하면") {
                Then("CommentNotFoundException이 발생해야 한다") {
                    shouldThrow<CommentNotFoundException> {
                        commentGetService.findCommentByIdNotDeleted(99L)
                    }
                    verify { commentRepository.findByCommentIdAndDeletedAtIsNull(99L) }
                }
            }
        }

        Given("피드에 여러 댓글이 존재할 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val feed = FeedTestFixture.createFeed(1L, user)
            val first = CommentTestFixture.createComment(1L, user, feed, "first")
            val second = CommentTestFixture.createComment(2L, user, feed, "second")

            every { commentRepository.findAllByFeedAndDeletedAtIsNullOrderByCreateDateDesc(feed) } returns
                listOf(first, second)

            When("피드의 모든 댓글을 조회하면") {
                val result = commentGetService.findAllByFeed(feed)

                Then("생성일 역순으로 정렬된 댓글 목록이 반환되어야 한다") {
                    result shouldContainExactly listOf(first, second)
                    verify { commentRepository.findAllByFeedAndDeletedAtIsNullOrderByCreateDateDesc(feed) }
                }
            }
        }
    })
