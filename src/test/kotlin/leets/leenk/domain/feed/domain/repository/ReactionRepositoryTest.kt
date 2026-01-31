package leets.leenk.domain.feed.domain.repository

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import leets.leenk.config.MysqlTestConfig
import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.entity.Reaction
import leets.leenk.domain.feed.test.FeedTestFixture
import leets.leenk.domain.feed.test.ReactionTestFixture
import leets.leenk.domain.feed.test.UserTestFixture
import leets.leenk.domain.user.domain.entity.User
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
@Import(MysqlTestConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReactionRepositoryTest(
    private val em: EntityManager,
    private val reactionRepository: ReactionRepository,
) : BehaviorSpec() {
    override fun extensions() = listOf(SpringTestExtension(SpringTestLifecycleMode.Root))

    init {
        Given("피드와 사용자에 대한 리액션이 존재할 때") {
            val author = persistUser(1L, "author")
            val me = persistUser(2L, "me")

            val feed = persistFeed(author)

            val r1 = reactionRepository.save(ReactionTestFixture.createReaction(feed, me, 7L))

            flushAndClear()

            When("피드와 사용자로 리액션을 조회하면") {
                val result = reactionRepository.findByFeedAndUser(feed, me).orElseThrow()

                Then("해당 리액션이 반환되어야 한다") {
                    result.id shouldBe r1.id
                    result.reactionCount shouldBe 7L
                }
            }
        }

        Given("한 피드에 여러 사용자의 리액션이 있을 때") {
            val author = persistUser(1L, "author")
            val u1 = persistUser(2L, "u1")
            val u2 = persistUser(3L, "u2")
            val u3 = persistUser(4L, "u3")

            val feed = persistFeed(author)

            reactionRepository.save(ReactionTestFixture.createReaction(feed, u1, 7L))
            reactionRepository.save(ReactionTestFixture.createReaction(feed, u2, 7L))
            reactionRepository.save(ReactionTestFixture.createReaction(feed, u3, 7L))

            flushAndClear()

            When("피드의 모든 리액션을 조회하면") {
                val result = reactionRepository.findAllByFeed(feed)

                Then("모든 리액션이 반환되어야 한다") {
                    result.size shouldBe 3
                    result.map(Reaction::reactionCount) shouldContainExactly listOf(7L, 7L, 7L)
                }
            }
        }
    }

    private fun flushAndClear() {
        em.flush()
        em.clear()
    }

    private fun persistUser(
        id: Long,
        name: String,
    ): User {
        val user = UserTestFixture.createUser(id, name)
        em.persist(user)
        return user
    }

    private fun persistFeed(user: User): Feed {
        val feed = FeedTestFixture.createFeed(null, user)
        em.persist(feed)
        return feed
    }
}
