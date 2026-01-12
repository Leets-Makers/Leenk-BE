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

    // todo: fun, String, Descrive, Behaivior spec으로 각각 작성해보기
    // todo: 코루틴으로 리팩토링
    // 피드에 락을 걸지 않은 경우 데드락 예외가 발생함을 확인 -> 코드 수정 -> 테스트 통과
    Given("여러 사용자가 동시에 같은 피드에 공감하는 경우") {
        val threadCount = 10

        val feedAuthor = userRepository.save(
            UserTestFixture.createUser(id = 100L, name = "피드작성자")
        )

        val feed = feedRepository.save(
            FeedTestFixture.createFeed(user = feedAuthor, description = "동시성 테스트용 피드")
        )

        val users = (1..threadCount).map { i ->
            userRepository.save(
                UserTestFixture.createUser(id = i.toLong(), name = "사용자$i")
            )
        }

        When("비관적 락을 사용하여 동시에 공감을 추가하면") {
            val executor = Executors.newFixedThreadPool(threadCount)
            val latch = CountDownLatch(threadCount)
            val successCount = AtomicInteger(0)
            val failureCount = AtomicInteger(0)

            repeat(threadCount) { index ->
                executor.submit {
                    try {
                        val userId = users[index].id!!
                        feedUsecase.reactToFeed(userId, feed.id!!, ReactionRequest(1L))
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

            Then("모든 요청이 성공하고 리액션 수가 정확해야 한다") {
                successCount.get() shouldBe threadCount
                failureCount.get() shouldBe 0

                val reactions = reactionRepository.findAllByFeed(feed)
                reactions.size shouldBe threadCount
            }
        }
    }

    // 피드, 유저에 락은 걸지 않은 경우 유니크 제약조건에 걸림. why: 유니크 제약 조건이 걸려있는데, 동시에 INSERT를 진행하기 때문
    // 코드 수정 -> 테스트 통과
    // 유저에 락을 걸어도 되지만, 피드에 먼저 락을 걸어주면 해당 문제는 해결됨
    Given("동일 사용자가 동시에 여러 번 공감하는 경우") {
        val attemptCount = 5

        val feedAuthor = userRepository.save(
            UserTestFixture.createUser(id = 200L, name = "피드작성자")
        )

        val user = userRepository.save(
            UserTestFixture.createUser(id = 201L, name = "공감사용자")
        )

        val feed = feedRepository.save(
            FeedTestFixture.createFeed(user = feedAuthor, description = "동일 사용자 동시성 테스트용 피드")
        )

        When("비관적 락을 사용하여 동시에 여러 번 공감을 추가하면") {
            val executor = Executors.newFixedThreadPool(attemptCount)
            val latch = CountDownLatch(attemptCount)
            val successCount = AtomicInteger(0)
            val failureCount = AtomicInteger(0)

            repeat(attemptCount) {
                executor.submit {
                    try {
                        feedUsecase.reactToFeed(user.id!!, feed.id!!, ReactionRequest(1L))
                        successCount.incrementAndGet()
                    } catch (e: Exception) {
                        failureCount.incrementAndGet()
                    } finally {
                        latch.countDown()
                    }
                }
            }
            latch.await()
            executor.shutdown()

            Then("모든 요청이 성공하고 리액션 카운트가 정확해야 한다") {
                successCount.get() shouldBe attemptCount
                failureCount.get() shouldBe 0

                val reaction = reactionRepository.findByFeedAndUser(feed, user)
                reaction.isPresent shouldBe true
                reaction.get().reactionCount shouldBe attemptCount.toLong()
            }
        }
    }

    // 피드, 유저에 락은 걸지 않은 경우 유니크 제약조건에 걸림 -> 코드 수정 -> 테스트 통과
    Given("다른 사용자가 동일한 사용자의 다른 피드에 동시에 공감하는 경우 (고강도 테스트)") {
        val threadCount = 10

        val feedAuthor = userRepository.saveAndFlush(
            UserTestFixture.createUser(id = 300L, name = "피드 작성자") // ID는 자동 생성 권장
        )

        val feed1 = feedRepository.saveAndFlush(
            FeedTestFixture.createFeed(id = 100L, user = feedAuthor)
        )

        val feed2 = feedRepository.saveAndFlush(
            FeedTestFixture.createFeed(id = 200L, user = feedAuthor)
        )

        // 각 스레드마다 다른 유저 생성 (ID: 301~310)
        val users = (1..threadCount).map { i ->
            userRepository.saveAndFlush(
                UserTestFixture.createUser(id = 300L + i, name = "사용자$i")
            )
        }

        When("비관적 락 환경에서 동시에 요청을 보낼 때") {
            val executor = Executors.newFixedThreadPool(threadCount)
            val latch = CountDownLatch(threadCount)

            val successCount = AtomicInteger(0)
            val failureCount = AtomicInteger(0)

            users.forEachIndexed { index, user ->
                executor.submit {
                    try {
                        // 홀수 유저는 피드1, 짝수 유저는 피드2를 공략
                        // 이렇게 하면 "동일 작성자(Author)" 테이블 쪽으로 트랜잭션이 동시에 몰리게 됨
                        val targetFeedId = if (index % 2 == 0) feed1.id!! else feed2.id!!

                        feedUsecase.reactToFeed(user.id!!, targetFeedId, ReactionRequest(1L))
                        successCount.incrementAndGet()
                    } catch (e: Exception) {
                        println("스레드 실패: ${e.message}")
                        failureCount.incrementAndGet()
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()
            executor.shutdown()

            Then("데드락 없이 모든 요청이 성공해야 한다") {
                successCount.get() shouldBe threadCount
                failureCount.get() shouldBe 0

                val reactions1 = reactionRepository.findAllByFeed(feed1)
                val reactions2 = reactionRepository.findAllByFeed(feed2)

                // 정확히 반반씩 들어갔는지 검증
                reactions1.size shouldBe (threadCount / 2) + (threadCount % 2) // 홀수 처리
                reactions2.size shouldBe (threadCount / 2)
            }
        }
    }
})
