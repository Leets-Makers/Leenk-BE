package leets.leenk.domain.feed.application.usecase

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import leets.leenk.config.MongoTestConfig
import leets.leenk.config.MysqlTestConfig
import leets.leenk.domain.feed.application.dto.request.ReactionRequest
import leets.leenk.domain.feed.domain.repository.FeedRepository
import leets.leenk.domain.feed.domain.repository.ReactionRepository
import leets.leenk.domain.feed.test.fixture.FeedTestFixture
import leets.leenk.domain.user.domain.repository.UserRepository
import leets.leenk.domain.user.test.fixture.UserTestFixture
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

private const val CONCURRENT_THREAD_COUNT = 10
private const val ATTEMPT_COUNT = 5

private const val FEED_AUTHOR_ID_100 = 100L
private const val FEED_AUTHOR_ID_200 = 200L
private const val FEED_AUTHOR_ID_300 = 300L

private const val USER_ID_201 = 201L
private const val USER_ID_BASE_300 = 300L

private const val FEED_ID_100 = 100L
private const val FEED_ID_200 = 200L

@SpringBootTest
@Import(MysqlTestConfig::class, MongoTestConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FeedUsecaseTest(
    private val feedUsecase: FeedUsecase,
    private val userRepository: UserRepository,
    private val feedRepository: FeedRepository,
    private val reactionRepository: ReactionRepository
) : BehaviorSpec({

    afterEach {
        reactionRepository.deleteAll()
        feedRepository.deleteAll()
        userRepository.deleteAll()
    }

    /**
     * 여러 사용자가 동시에 같은 피드에 공감할 때 비관적 락으로 동시성을 제어하는지 검증
     * 비관적 락이 없으면 데드락이 발생할 수 있음
     */
    Given("여러 사용자가 동시에 같은 피드에 공감하는 경우") {
        val feedAuthor = userRepository.save(
            UserTestFixture.createUser(id = FEED_AUTHOR_ID_100, name = "피드작성자")
        )

        val feed = feedRepository.save(
            FeedTestFixture.createFeed(user = feedAuthor, description = "동시성 테스트용 피드")
        )

        val users = (1..CONCURRENT_THREAD_COUNT).map { i ->
            userRepository.save(
                UserTestFixture.createUser(id = i.toLong(), name = "사용자$i")
            )
        }

        When("비관적 락을 사용하여 동시에 공감을 추가하면") {
            val (successCount, failureCount) = executeConcurrentReactions(
                threadCount = CONCURRENT_THREAD_COUNT,
                reactions = users.map { user ->
                    { feedUsecase.reactToFeed(user.id!!, feed.id!!, ReactionRequest(1L)) }
                }
            )

            Then("모든 요청이 성공하고 리액션 수가 정확해야 한다") {
                successCount shouldBe CONCURRENT_THREAD_COUNT
                failureCount shouldBe 0

                val reactions = reactionRepository.findAllByFeed(feed)
                reactions.size shouldBe CONCURRENT_THREAD_COUNT

                // 영속성 컨텍스트에서 최신 데이터 조회
                val updatedFeedAuthor = userRepository.findById(feedAuthor.id!!).get()
                updatedFeedAuthor.totalReactionCount shouldBe CONCURRENT_THREAD_COUNT.toLong()

                val updatedFeed = feedRepository.findById(feed.id!!).get()
                updatedFeed.totalReactionCount shouldBe CONCURRENT_THREAD_COUNT.toLong()
            }
        }
    }

    /**
     * 동일 사용자가 여러 번 공감할 때 reactionCount가 정확하게 증가하는지 검증
     * 피드에 먼저 락을 걸어 유니크 제약 조건 위반(동시 INSERT)을 방지
     */
    Given("동일 사용자가 동시에 여러 번 공감하는 경우") {
        val feedAuthor = userRepository.save(
            UserTestFixture.createUser(id = FEED_AUTHOR_ID_200, name = "피드작성자")
        )

        val user = userRepository.save(
            UserTestFixture.createUser(id = USER_ID_201, name = "공감사용자")
        )

        val feed = feedRepository.save(
            FeedTestFixture.createFeed(user = feedAuthor, description = "동일 사용자 동시성 테스트용 피드")
        )

        When("비관적 락을 사용하여 동시에 여러 번 공감을 추가하면") {
            val (successCount, failureCount) = executeConcurrentReactions(
                threadCount = ATTEMPT_COUNT,
                reactions = List(ATTEMPT_COUNT) {
                    { feedUsecase.reactToFeed(user.id!!, feed.id!!, ReactionRequest(1L)) }
                }
            )

            Then("모든 요청이 성공하고 리액션 카운트가 정확해야 한다") {
                successCount shouldBe ATTEMPT_COUNT
                failureCount shouldBe 0

                val reaction = reactionRepository.findByFeedAndUser(feed, user)
                reaction.isPresent shouldBe true
                reaction.get().reactionCount shouldBe ATTEMPT_COUNT.toLong()

                // 영속성 컨텍스트에서 최신 데이터 조회
                val updatedFeedAuthor = userRepository.findById(feedAuthor.id!!).get()
                updatedFeedAuthor.totalReactionCount shouldBe ATTEMPT_COUNT.toLong()

                val updatedFeed = feedRepository.findById(feed.id!!).get()
                updatedFeed.totalReactionCount shouldBe ATTEMPT_COUNT.toLong()
            }
        }
    }

    /**
     * 동일 작성자의 여러 피드에 여러 사용자가 동시에 공감할 때 데드락이 발생하지 않는지 검증
     * 작성자에 락이 걸리지 않은 경우 데드락이 발생할 수 있음
     */
    Given("다른 사용자가 동일한 사용자의 다른 피드에 동시에 공감하는 경우 (고강도 테스트)") {
        val feedAuthor = userRepository.saveAndFlush(
            UserTestFixture.createUser(id = FEED_AUTHOR_ID_300, name = "피드 작성자")
        )

        val feed1 = feedRepository.saveAndFlush(
            FeedTestFixture.createFeed(id = FEED_ID_100, user = feedAuthor)
        )

        val feed2 = feedRepository.saveAndFlush(
            FeedTestFixture.createFeed(id = FEED_ID_200, user = feedAuthor)
        )

        val users = (1..CONCURRENT_THREAD_COUNT).map { i ->
            userRepository.saveAndFlush(
                UserTestFixture.createUser(id = USER_ID_BASE_300 + i, name = "사용자$i")
            )
        }

        When("비관적 락 환경에서 동시에 요청을 보낼 때") {
            val (successCount, failureCount) = executeConcurrentReactions(
                threadCount = CONCURRENT_THREAD_COUNT,
                reactions = users.mapIndexed { index, user ->
                    // 짝수/홀수 인덱스로 피드를 나누어 동일 작성자의 서로 다른 피드로 트랜잭션 분산
                    val targetFeedId = if (index % 2 == 0) feed1.id!! else feed2.id!!
                    { feedUsecase.reactToFeed(user.id!!, targetFeedId, ReactionRequest(1L)) }
                }
            )

            Then("데드락 없이 모든 요청이 성공해야 한다") {
                successCount shouldBe CONCURRENT_THREAD_COUNT
                failureCount shouldBe 0

                val reactions1 = reactionRepository.findAllByFeed(feed1)
                val reactions2 = reactionRepository.findAllByFeed(feed2)

                reactions1.size shouldBe (CONCURRENT_THREAD_COUNT / 2 + CONCURRENT_THREAD_COUNT % 2)
                reactions2.size shouldBe CONCURRENT_THREAD_COUNT / 2

                // 영속성 컨텍스트에서 최신 데이터 조회
                val updatedFeedAuthor = userRepository.findById(feedAuthor.id!!).get()
                updatedFeedAuthor.totalReactionCount shouldBe CONCURRENT_THREAD_COUNT.toLong()

                val updatedFeed1 = feedRepository.findById(feed1.id!!).get()
                val updatedFeed2 = feedRepository.findById(feed2.id!!).get()
                (updatedFeed1.totalReactionCount + updatedFeed2.totalReactionCount) shouldBe CONCURRENT_THREAD_COUNT.toLong()
            }
        }
    }
})

/**
 * 동시성 테스트를 위한 헬퍼 함수
 *
 * @param threadCount 스레드 풀 크기
 * @param reactions 실행할 작업 목록
 * @return Pair<성공 횟수, 실패 횟수>
 */
private fun executeConcurrentReactions(
    threadCount: Int,
    reactions: List<() -> Unit>
): Pair<Int, Int> {
    val executor = Executors.newFixedThreadPool(threadCount)
    val latch = CountDownLatch(reactions.size)
    val successCount = AtomicInteger(0)
    val failureCount = AtomicInteger(0)

    reactions.forEachIndexed { index, reaction ->
        executor.submit {
            try {
                reaction()
                successCount.incrementAndGet()
            } catch (e: Exception) {
                println("스레드 $index 실패: ${e.message}")
                failureCount.incrementAndGet()
            } finally {
                latch.countDown()
            }
        }
    }

    latch.await()
    executor.shutdown()

    return successCount.get() to failureCount.get()
}
