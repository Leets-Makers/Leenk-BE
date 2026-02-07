package leets.leenk.domain.feed.domain.repository

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.persistence.EntityManager
import leets.leenk.config.MysqlTestConfig
import leets.leenk.domain.feed.test.FeedTestFixture
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
class FeedRepositoryTest(
    private val em: EntityManager,
    private val feedRepository: FeedRepository,
) : BehaviorSpec() {
    override fun extensions() = listOf(SpringTestExtension(SpringTestLifecycleMode.Root))

    init {
        Given("여러 사용자의 피드가 있고 일부는 삭제되거나 차단되었을 때") {
            val u1 = persistUser(null, "me")
            val u2 = persistUser(null, "me2")

            val base = LocalDateTime.of(2025, 12, 22, 15, 0)

            val f1 = feedRepository.save(FeedTestFixture.createFeed(null, u1))
            val f2 = feedRepository.save(FeedTestFixture.createFeed(null, u1))
            val f3 = feedRepository.save(FeedTestFixture.createFeed(null, u1))
            val block = feedRepository.save(FeedTestFixture.createFeed(null, u2))
            val delete = feedRepository.save(FeedTestFixture.createFeed(null, u1))

            flushAndClear()

            updateFeedDates(f1.id!!, base.plusMinutes(1), null)
            updateFeedDates(f2.id!!, base.plusMinutes(2), null)
            updateFeedDates(f3.id!!, base.plusMinutes(3), null)
            updateFeedDates(block.id!!, base.plusMinutes(4), null)
            updateFeedDates(delete.id!!, base.plusMinutes(5), base.plusMinutes(6))

            flushAndClear()

            When("차단된 사용자를 제외하고 삭제되지 않은 피드를 조회하면") {
                val slice =
                    feedRepository.findAllByDeletedAtIsNullWithUser(
                        PageRequest.of(0, 2),
                        listOf(u2.id!!),
                    )

                Then("차단되지 않고 삭제되지 않은 피드만 최신순으로 반환되어야 한다") {
                    slice.content shouldHaveSize 2
                    slice.hasNext() shouldBe true
                    slice.content.map { it.id } shouldContainExactly listOf(f3.id, f2.id)
                    slice.content.forEach { feed ->
                        feed.deletedAt.shouldBeNull()
                        feed.user.id shouldNotBe u2.id
                    }
                }
            }
        }

        Given("특정 사용자의 피드가 여러 개 있고 일부는 삭제되었을 때") {
            val me = persistUser(null, "me")
            val other = persistUser(null, "me2")

            val base = LocalDateTime.of(2025, 12, 22, 16, 0)

            val mine1 = feedRepository.save(FeedTestFixture.createFeed(null, me))
            val mine2 = feedRepository.save(FeedTestFixture.createFeed(null, me))
            val mineDeleted = feedRepository.save(FeedTestFixture.createFeed(null, me))
            val other1 = feedRepository.save(FeedTestFixture.createFeed(null, other))

            flushAndClear()

            updateFeedDates(mine1.id!!, base.plusMinutes(1), null)
            updateFeedDates(mine2.id!!, base.plusMinutes(2), null)
            updateFeedDates(mineDeleted.id!!, base.plusMinutes(3), base.plusMinutes(4))
            updateFeedDates(other1.id!!, base.plusMinutes(4), null)

            flushAndClear()

            When("사용자의 삭제되지 않은 피드를 조회하면") {
                val slice = feedRepository.findAllByUserAndDeletedAtIsNull(me, PageRequest.of(0, 10))

                Then("해당 사용자의 삭제되지 않은 피드만 최신순으로 반환되어야 한다") {
                    slice.content shouldHaveSize 2
                    slice.content.map { it.id } shouldContainExactly listOf(mine2.id, mine1.id)
                    slice.content.forEach { feed ->
                        feed.user.name shouldBe me.name
                        feed.deletedAt.shouldBeNull()
                    }
                }
            }
        }

        Given("삭제되지 않은 피드와 삭제된 피드가 존재할 때") {
            val me = persistUser(null, "me")

            val base = LocalDateTime.of(2025, 12, 22, 16, 0)

            val notDeleted = feedRepository.save(FeedTestFixture.createFeed(null, me))
            val deleted = feedRepository.save(FeedTestFixture.createFeed(null, me))

            flushAndClear()

            updateFeedDates(deleted.id!!, base.plusMinutes(1), base.plusMinutes(2))
            flushAndClear()

            When("ID로 삭제되지 않은 피드만 조회하면") {
                val notDeletedFeed = feedRepository.findByDeletedAtIsNullAndId(notDeleted.id!!)
                val deletedFeed = feedRepository.findByDeletedAtIsNullAndId(deleted.id)

                Then("삭제되지 않은 피드만 조회되어야 한다") {
                    notDeletedFeed.shouldBePresent {
                        it.id shouldBe notDeleted.id
                    }
                    deletedFeed.isEmpty shouldBe true
                }
            }
        }

        Given("기준 시간 이후의 피드들이 있고 일부는 삭제되거나 차단되었을 때") {
            val me = persistUser(null, "me")
            val blockedUser = persistUser(null, "blocked")

            val base = LocalDateTime.of(2025, 12, 22, 16, 0)

            val feed1 = feedRepository.save(FeedTestFixture.createFeed(null, me))
            val feed2 = feedRepository.save(FeedTestFixture.createFeed(null, me))
            val feedDeleted = feedRepository.save(FeedTestFixture.createFeed(null, me))
            val feed3 = feedRepository.save(FeedTestFixture.createFeed(null, me))
            val feedBlocked = feedRepository.save(FeedTestFixture.createFeed(null, blockedUser))

            flushAndClear()

            updateFeedDates(feed1.id!!, base.plusMinutes(1), null)
            updateFeedDates(feed2.id!!, base.plusMinutes(1), null)
            updateFeedDates(feedDeleted.id!!, base.plusMinutes(2), base.plusMinutes(4))
            updateFeedDates(feed3.id!!, base.plusMinutes(3), null)
            updateFeedDates(feedBlocked.id!!, base.plusMinutes(4), null)

            flushAndClear()

            When("기준 시간 이후의 피드를 조회하면") {
                val prevFeeds =
                    feedRepository.findPrevFeeds(
                        base,
                        listOf(blockedUser.id!!),
                        PageRequest.of(0, 2),
                    )

                Then("차단되지 않고 삭제되지 않은 이후 피드만 반환되어야 한다") {
                    prevFeeds shouldHaveSize 2
                    prevFeeds.map { it.id } shouldContainExactly listOf(feed1.id, feed2.id)
                    prevFeeds.forEach { feed ->
                        feed.createDate shouldBeGreaterThan base
                        feed.deletedAt.shouldBeNull()
                        feed.user.id shouldNotBe blockedUser.id
                    }
                }
            }
        }

        Given("기준 시간 이전의 피드들이 있고 일부는 삭제되거나 차단되었을 때") {
            val me = persistUser(null, "me")
            val blockedUser = persistUser(null, "blocked")

            val base = LocalDateTime.of(2025, 12, 22, 16, 0)

            val feed1 = feedRepository.save(FeedTestFixture.createFeed(null, me))
            val feed2 = feedRepository.save(FeedTestFixture.createFeed(null, me))
            val feedDeleted = feedRepository.save(FeedTestFixture.createFeed(null, me))
            val feedBlocked = feedRepository.save(FeedTestFixture.createFeed(null, blockedUser))
            val feed3 = feedRepository.save(FeedTestFixture.createFeed(null, me))

            flushAndClear()

            updateFeedDates(feed1.id!!, base.minusMinutes(10), null)
            updateFeedDates(feed2.id!!, base.minusMinutes(20), null)
            updateFeedDates(feedDeleted.id!!, base.minusMinutes(30), base.minusMinutes(29))
            updateFeedDates(feedBlocked.id!!, base.minusMinutes(40), null)
            updateFeedDates(feed3.id!!, base.minusMinutes(10), null)

            flushAndClear()

            When("기준 시간 이전의 피드를 조회하면") {
                val nextFeeds =
                    feedRepository.findNextFeeds(
                        base,
                        listOf(blockedUser.id!!),
                        PageRequest.of(0, 2),
                    )

                Then("차단되지 않고 삭제되지 않은 이전 피드만 반환되어야 한다") {
                    nextFeeds shouldHaveSize 2
                    nextFeeds.map { it.id } shouldContainExactly listOf(feed1.id, feed3.id)
                    nextFeeds.forEach { feed ->
                        feed.createDate shouldBeLessThan base
                        feed.deletedAt.shouldBeNull()
                        feed.user.id shouldNotBe blockedUser.id
                    }
                }
            }
        }
    }

    private fun updateFeedDates(
        feedId: Long,
        createdDate: LocalDateTime,
        deletedAt: LocalDateTime?,
    ) {
        em
            .createQuery("UPDATE Feed f SET f.createDate = :createDate, f.deletedAt = :deletedAt WHERE f.id = :id")
            .setParameter("createDate", createdDate)
            .setParameter("deletedAt", deletedAt)
            .setParameter("id", feedId)
            .executeUpdate()
    }

    private fun persistUser(
        id: Long?,
        name: String,
    ): User {
        val user = UserTestFixture.createUser(id, name)
        em.persist(user)
        return user
    }

    private fun flushAndClear() {
        em.flush()
        em.clear()
    }
}
