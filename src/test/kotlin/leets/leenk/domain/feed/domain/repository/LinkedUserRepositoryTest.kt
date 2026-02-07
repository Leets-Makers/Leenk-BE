package leets.leenk.domain.feed.domain.repository

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import leets.leenk.config.MysqlTestConfig
import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.test.FeedTestFixture
import leets.leenk.domain.feed.test.LinkedUserTestFixture
import leets.leenk.domain.feed.test.UserTestFixture
import leets.leenk.domain.user.domain.entity.User
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@DataJpaTest
@ActiveProfiles("test")
@Import(MysqlTestConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LinkedUserRepositoryTest(
    private val em: EntityManager,
    private val linkedUserRepository: LinkedUserRepository,
) : BehaviorSpec() {
    override fun extensions() = listOf(SpringTestExtension(SpringTestLifecycleMode.Root))

    init {
        Given("한 피드에 여러 사용자가 연결되어 있을 때") {
            val author = persistUser(null, "author")
            val u1 = persistUser(null, "u1")
            val u2 = persistUser(null, "u2")
            val u3 = persistUser(null, "u3")

            val feed = persistFeed(author)

            val l1 = linkedUserRepository.save(LinkedUserTestFixture.createLinkedUser(null, u1, feed))
            val l2 = linkedUserRepository.save(LinkedUserTestFixture.createLinkedUser(null, u2, feed))
            val l3 = linkedUserRepository.save(LinkedUserTestFixture.createLinkedUser(null, u3, feed))

            flushAndClear()

            When("피드로 연결된 사용자를 조회하면") {
                val result = linkedUserRepository.findAllByFeed(feed)

                Then("모든 연결된 사용자가 반환되어야 한다") {
                    result.size shouldBe 3
                    result.map { it.id } shouldContainExactly listOf(l1.id, l2.id, l3.id)
                }
            }
        }

        Given("사용자가 여러 피드에 연결되어 있고 일부는 삭제되었을 때") {
            val me = persistUser(null, "me")
            val other = persistUser(null, "other")

            val base = LocalDateTime.of(2025, 12, 22, 16, 0)

            val myFeed =
                saveFeedWithCreateDate(
                    FeedTestFixture.createFeed(null, me),
                    base.plusMinutes(1),
                    null,
                )
            val f1 =
                saveFeedWithCreateDate(
                    FeedTestFixture.createFeed(null, other),
                    base.plusMinutes(2),
                    null,
                )
            val deleted =
                saveFeedWithCreateDate(
                    FeedTestFixture.createFeed(null, other),
                    base.plusMinutes(3),
                    base.plusMinutes(4),
                )

            linkedUserRepository.save(LinkedUserTestFixture.createLinkedUser(null, me, myFeed))
            linkedUserRepository.save(LinkedUserTestFixture.createLinkedUser(null, me, f1))
            linkedUserRepository.save(LinkedUserTestFixture.createLinkedUser(null, me, deleted))

            flushAndClear()

            When("사용자로 연결된 피드를 조회하면") {
                val slice = linkedUserRepository.findFeedsByLinkedUser(me, PageRequest.of(0, 10))

                Then("작성자가 자신이 아닌 삭제되지 않은 피드만 반환되어야 한다") {
                    slice shouldHaveSize 1
                    slice.content.map { it.id } shouldContainExactly listOf(f1.id)
                }
            }
        }

        Given("여러 피드에 연결된 사용자가 있을 때") {
            val author = persistUser(null, "author")
            val u1 = persistUser(null, "u1")
            val u2 = persistUser(null, "u2")

            val f1 = persistFeed(author)
            val f2 = persistFeed(author)

            linkedUserRepository.save(LinkedUserTestFixture.createLinkedUser(null, u1, f1))
            linkedUserRepository.save(LinkedUserTestFixture.createLinkedUser(null, u1, f2))
            linkedUserRepository.save(LinkedUserTestFixture.createLinkedUser(null, u2, f1))

            flushAndClear()

            When("특정 피드의 연결된 사용자를 모두 삭제하면") {
                linkedUserRepository.deleteAllByFeed(f1)
                flushAndClear()

                Then("해당 피드의 연결만 삭제되고 다른 피드는 유지되어야 한다") {
                    linkedUserRepository.findAllByFeed(f1).shouldBeEmpty()
                    linkedUserRepository.findAllByFeed(f2) shouldHaveSize 1
                }
            }
        }
    }

    private fun saveFeedWithCreateDate(
        feed: Feed,
        createDate: LocalDateTime,
        deletedAt: LocalDateTime?,
    ): Feed {
        val saved = persistFeed(feed.user)
        flushAndClear()

        em
            .createQuery("UPDATE Feed f SET f.createDate = :createDate, f.deletedAt = :deletedAt WHERE f.id = :id")
            .setParameter("createDate", createDate)
            .setParameter("deletedAt", deletedAt)
            .setParameter("id", saved.id)
            .executeUpdate()
        flushAndClear()

        return saved
    }

    private fun persistUser(
        id: Long?,
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

    private fun flushAndClear() {
        em.flush()
        em.clear()
    }
}
