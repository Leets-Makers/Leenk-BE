package leets.leenk.domain.feed.domain.repository

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import leets.leenk.config.MysqlTestConfig
import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.test.CommentTestFixture
import leets.leenk.domain.feed.test.FeedTestFixture
import leets.leenk.domain.feed.test.UserTestFixture
import leets.leenk.domain.user.domain.entity.User
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@DataJpaTest
@ActiveProfiles("test")
@Import(MysqlTestConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest(
    private val em: EntityManager,
    private val commentRepository: CommentRepository,
) : BehaviorSpec() {
    override fun extensions() = listOf(SpringTestExtension(SpringTestLifecycleMode.Root))

    init {
        Given("삭제되지 않은 댓글과 삭제된 댓글이 존재할 때") {
            val author = persistUser()
            val feed = persistFeed(author)

            val notDeleted = commentRepository.save(CommentTestFixture.createComment(null, author, feed, "hi1"))
            val deleted = commentRepository.save(CommentTestFixture.createComment(null, author, feed, "hi2"))

            flushAndClear()

            updateCommentDates(notDeleted.commentId!!, BASE_TIME.plusMinutes(1), null)
            updateCommentDates(deleted.commentId!!, BASE_TIME.plusMinutes(2), BASE_TIME.plusMinutes(1))

            flushAndClear()

            When("ID로 삭제되지 않은 댓글만 조회하면") {
                val notDeletedComment = commentRepository.findByCommentIdAndDeletedAtIsNull(notDeleted.commentId)
                val deletedComment = commentRepository.findByCommentIdAndDeletedAtIsNull(deleted.commentId)

                Then("삭제되지 않은 댓글만 조회되어야 한다") {
                    notDeletedComment.shouldBePresent {
                        it.comment shouldBe notDeleted.comment
                    }
                    deletedComment.isEmpty shouldBe true
                }
            }
        }

        Given("한 피드에 여러 댓글이 있고 일부는 삭제되었을 때") {
            val author = persistUser()
            val feed1 = persistFeed(author)
            val feed2 = persistFeed(author)

            val c1 = commentRepository.save(CommentTestFixture.createComment(null, author, feed1, "hi1"))
            val c2 = commentRepository.save(CommentTestFixture.createComment(null, author, feed1, "hi2"))
            val c3Deleted = commentRepository.save(CommentTestFixture.createComment(null, author, feed1, "hi3"))
            val other = commentRepository.save(CommentTestFixture.createComment(null, author, feed2, "other"))

            flushAndClear()

            updateCommentDates(c1.commentId!!, BASE_TIME.plusMinutes(1), null)
            updateCommentDates(c2.commentId!!, BASE_TIME.plusMinutes(2), null)
            updateCommentDates(c3Deleted.commentId!!, BASE_TIME.plusMinutes(3), BASE_TIME.plusMinutes(3))
            updateCommentDates(other.commentId!!, BASE_TIME.plusMinutes(4), null)

            flushAndClear()

            When("피드의 삭제되지 않은 댓글을 생성일 역순으로 조회하면") {
                val result = commentRepository.findAllByFeedAndDeletedAtIsNullOrderByCreateDateDesc(feed1)

                Then("해당 피드의 삭제되지 않은 댓글만 최신순으로 반환되어야 한다") {
                    result shouldHaveSize 2
                    result.map { it.commentId } shouldContainExactly listOf(c2.commentId, c1.commentId)
                    result.forEach { comment ->
                        comment.feed.id shouldBe feed1.id
                        comment.deletedAt.shouldBeNull()
                    }
                }
            }
        }
    }

    private fun updateCommentDates(
        commentId: Long,
        createdDate: LocalDateTime,
        deletedAt: LocalDateTime?,
    ) {
        em
            .createQuery(
                "UPDATE Comment c SET c.createDate = :createDate, c.deletedAt = :deletedAt WHERE c.commentId = :id",
            ).setParameter("createDate", createdDate)
            .setParameter("deletedAt", deletedAt)
            .setParameter("id", commentId)
            .executeUpdate()
    }

    private fun persistUser(): User {
        val user = UserTestFixture.createUser(null, "me")
        em.persist(user)
        return user
    }

    private fun persistFeed(user: User): Feed {
        val feed = FeedTestFixture.createFeed(null, user)
        em.persist(feed)
        return feed
    }

    private fun flushAndClear() {
        em.flush()
        em.clear()
    }

    companion object {
        private val BASE_TIME = LocalDateTime.of(2025, 12, 22, 16, 0)
    }
}
