package leets.leenk.domain.media.domain.repository

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.optional.shouldBeEmpty
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import leets.leenk.config.MysqlTestConfig
import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.repository.FeedRepository
import leets.leenk.domain.feed.test.FeedTestFixture
import leets.leenk.domain.feed.test.UserTestFixture
import leets.leenk.domain.leenk.domain.entity.Leenk
import leets.leenk.domain.leenk.domain.repository.LeenkRepository
import leets.leenk.domain.leenk.test.fixture.LeenkTestFixture
import leets.leenk.domain.leenk.test.fixture.LocationTestFixture
import leets.leenk.domain.media.domain.entity.Media
import leets.leenk.domain.media.domain.entity.enums.MediaType
import leets.leenk.domain.user.domain.entity.User
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
@Import(MysqlTestConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MediaRepositoryTest(
    private val em: EntityManager,
    private val mediaRepository: MediaRepository,
    private val feedRepository: FeedRepository,
    private val leenkRepository: LeenkRepository,
) : BehaviorSpec() {
    override fun extensions() = listOf(SpringTestExtension(SpringTestLifecycleMode.Root))

    init {
        Given("여러 Feed에 position 순서가 다른 Media들이 저장되어 있을 때") {
            val user = persistUser("user1")
            val feed1 = persistFeed(user)
            val feed2 = persistFeed(user)

            val media1 = persistMedia(mediaUrl = "https://cdn.example.com/1.jpg", position = 2, feed = feed1)
            val media2 = persistMedia(mediaUrl = "https://cdn.example.com/2.jpg", position = 1, feed = feed1)
            val media3 = persistMedia(mediaUrl = "https://cdn.example.com/3.jpg", position = 1, feed = feed2)

            flushAndClear()

            When("여러 Feed로 Media를 조회하면") {
                val result = mediaRepository.findAllByFeedInOrderByPosition(listOf(feed1, feed2))

                Then("모든 Media가 반환되고 각 Feed 내에서 position 오름차순으로 정렬되어야 한다") {
                    result shouldHaveSize 3
                    // feed1의 미디어는 position 오름차순(media2 먼저, media1 다음), feed2는 media3
                    val feed1Medias = result.filter { it.feed?.id == feed1.id }
                    val feed2Medias = result.filter { it.feed?.id == feed2.id }
                    feed1Medias shouldHaveSize 2
                    feed1Medias.map { it.id } shouldContainExactly listOf(media2.id, media1.id)
                    feed2Medias shouldHaveSize 1
                    feed2Medias.map { it.id } shouldContainExactly listOf(media3.id)
                }
            }

            When("특정 Feed로 Media를 조회하면") {
                val result = mediaRepository.findAllByFeedOrderByPosition(feed1)

                Then("해당 Feed의 Media만 position 오름차순으로 반환되어야 한다") {
                    result shouldHaveSize 2
                    result.map { it.id } shouldContainExactly listOf(media2.id, media1.id)
                }
            }
        }

        Given("Leenk에 여러 Media가 position 순서가 다르게 저장되어 있을 때") {
            val user = persistUser("user2")
            val leenk = persistLeenk(user)

            val media1 = persistMedia(mediaUrl = "https://cdn.example.com/a.jpg", position = 3, leenk = leenk)
            val media2 = persistMedia(mediaUrl = "https://cdn.example.com/b.jpg", position = 1, leenk = leenk)
            val media3 = persistMedia(mediaUrl = "https://cdn.example.com/c.jpg", position = 2, leenk = leenk)

            flushAndClear()

            When("Leenk로 첫 번째 Media를 조회하면") {
                val result = mediaRepository.findFirstByLeenkOrderByPositionAsc(leenk)

                Then("position이 가장 낮은 Media가 반환되어야 한다") {
                    result.shouldBePresent {
                        it.id shouldBe media2.id
                        it.position shouldBe 1
                    }
                }
            }

            When("Leenk로 모든 Media를 조회하면") {
                val result = mediaRepository.findAllByLeenkOrderByPosition(leenk)

                Then("position 오름차순으로 정렬된 모든 Media가 반환되어야 한다") {
                    result shouldHaveSize 3
                    result.map { it.id } shouldContainExactly listOf(media2.id, media3.id, media1.id)
                }
            }
        }

        Given("여러 Leenk에 Media가 저장되어 있을 때") {
            val user = persistUser("user3")
            val leenk1 = persistLeenk(user)
            val leenk2 = persistLeenk(user)

            val media1 = persistMedia(mediaUrl = "https://cdn.example.com/x.jpg", position = 2, leenk = leenk1)
            val media2 = persistMedia(mediaUrl = "https://cdn.example.com/y.jpg", position = 1, leenk = leenk1)
            val media3 = persistMedia(mediaUrl = "https://cdn.example.com/z.jpg", position = 1, leenk = leenk2)

            flushAndClear()

            When("여러 Leenk로 Media를 조회하면") {
                val result = mediaRepository.findAllByLeenkInOrderByPosition(listOf(leenk1, leenk2))

                Then("모든 Media가 반환되고 각 Leenk 내에서 position 오름차순으로 정렬되어야 한다") {
                    result shouldHaveSize 3
                    val leenk1Medias = result.filter { it.leenk?.id == leenk1.id }
                    val leenk2Medias = result.filter { it.leenk?.id == leenk2.id }
                    leenk1Medias shouldHaveSize 2
                    leenk1Medias.map { it.id } shouldContainExactly listOf(media2.id, media1.id)
                    leenk2Medias shouldHaveSize 1
                    leenk2Medias.map { it.id } shouldContainExactly listOf(media3.id)
                }
            }
        }

        Given("특정 mediaUrl을 가진 Media가 저장되어 있을 때") {
            val targetUrl = "https://cdn.example.com/unique-target.jpg"
            val user = persistUser("user4")
            val feed = persistFeed(user)

            persistMedia(mediaUrl = targetUrl, position = 1, feed = feed)

            flushAndClear()

            When("존재하는 URL로 조회하면") {
                val result = mediaRepository.findByMediaUrl(targetUrl)

                Then("해당 Media가 반환되어야 한다") {
                    result.shouldBePresent {
                        it.mediaUrl shouldBe targetUrl
                    }
                }
            }

            When("존재하지 않는 URL로 조회하면") {
                val result = mediaRepository.findByMediaUrl("https://cdn.example.com/not-exist.jpg")

                Then("빈 Optional이 반환되어야 한다") {
                    result.shouldBeEmpty()
                }
            }
        }

        Given("Feed에 Media가 저장되어 있을 때") {
            val user = persistUser("user5")
            val feed = persistFeed(user)

            persistMedia(mediaUrl = "https://cdn.example.com/del1.jpg", position = 1, feed = feed)
            persistMedia(mediaUrl = "https://cdn.example.com/del2.jpg", position = 2, feed = feed)

            flushAndClear()

            When("해당 Feed의 모든 Media를 삭제하면") {
                mediaRepository.deleteAllByFeed(feed)
                flushAndClear()

                Then("해당 Feed의 Media가 모두 삭제되어야 한다") {
                    val remaining = mediaRepository.findAllByFeedOrderByPosition(feed)
                    remaining shouldHaveSize 0
                }
            }
        }
    }

    private fun persistUser(name: String): User {
        val user = UserTestFixture.createUser(null, name)
        em.persist(user)
        return user
    }

    private fun persistFeed(user: User): Feed = feedRepository.save(FeedTestFixture.createFeed(null, user))

    private fun persistLeenk(user: User): Leenk {
        val location = LocationTestFixture.createLocation()
        em.persist(location)
        val leenk = LeenkTestFixture.createLeenk(id = null, author = user, location = location)
        return leenkRepository.save(leenk)
    }

    private fun persistMedia(
        mediaUrl: String,
        position: Int,
        feed: Feed? = null,
        leenk: Leenk? = null,
    ): Media {
        val media =
            Media(
                mediaUrl = mediaUrl,
                thumbnailUrl = mediaUrl,
                mediaType = MediaType.IMAGE,
                position = position,
                feed = feed,
                leenk = leenk,
            )
        return mediaRepository.save(media)
    }

    private fun flushAndClear() {
        em.flush()
        em.clear()
    }
}
