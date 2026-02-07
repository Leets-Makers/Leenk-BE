package leets.leenk.domain.feed.domain.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.feed.application.exception.FeedNotFoundException
import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.repository.FeedRepository
import leets.leenk.domain.feed.test.FeedTestFixture
import leets.leenk.domain.feed.test.UserTestFixture
import leets.leenk.domain.user.domain.entity.UserBlock
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.SliceImpl
import java.time.LocalDateTime
import java.util.Optional

class FeedGetServiceTest :
    BehaviorSpec({
        val feedRepository = mockk<FeedRepository>()
        val feedGetService = FeedGetService(feedRepository)

        Given("삭제되지 않은 피드가 존재할 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val feed = FeedTestFixture.createFeed(1L, user)

            every { feedRepository.findByDeletedAtIsNullAndId(1L) } returns Optional.of(feed)

            When("ID로 피드를 조회하면") {
                val result = feedGetService.findById(1L)

                Then("해당 피드가 반환되어야 한다") {
                    result shouldBe feed
                    verify { feedRepository.findByDeletedAtIsNullAndId(1L) }
                }
            }
        }

        Given("삭제되지 않은 피드가 존재하지 않을 때") {
            every { feedRepository.findByDeletedAtIsNullAndId(99L) } returns Optional.empty()

            When("ID로 피드를 조회하면") {
                Then("FeedNotFoundException이 발생해야 한다") {
                    shouldThrow<FeedNotFoundException> {
                        feedGetService.findById(99L)
                    }
                    verify { feedRepository.findByDeletedAtIsNullAndId(99L) }
                }
            }
        }

        Given("차단된 사용자 목록이 있을 때") {
            val blocker = UserTestFixture.createUser(1L, "blocker")
            val blocked1 = UserTestFixture.createUser(2L, "blocked1")
            val blocked2 = UserTestFixture.createUser(3L, "blocked2")
            val blockedUsers =
                listOf(
                    UserBlock
                        .builder()
                        .blocker(blocker)
                        .blocked(blocked1)
                        .build(),
                    UserBlock
                        .builder()
                        .blocker(blocker)
                        .blocked(blocked2)
                        .build(),
                )

            val pageable = PageRequest.of(0, 10)
            val slice = SliceImpl<Feed>(emptyList())

            every { feedRepository.findAllByDeletedAtIsNullWithUser(pageable, listOf(2L, 3L)) } returns slice

            When("전체 피드를 조회하면") {
                val result = feedGetService.findAll(pageable, blockedUsers)

                Then("차단된 사용자를 제외한 피드 목록이 반환되어야 한다") {
                    result shouldBe slice
                    verify { feedRepository.findAllByDeletedAtIsNullWithUser(pageable, listOf(2L, 3L)) }
                }
            }
        }

        Given("특정 사용자의 피드를 조회할 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val pageable = PageRequest.of(0, 5)
            val slice = SliceImpl<Feed>(emptyList())

            every { feedRepository.findAllByUserAndDeletedAtIsNull(user, pageable) } returns slice

            When("사용자의 피드를 조회하면") {
                val result = feedGetService.findAllByUser(user, pageable)

                Then("해당 사용자의 피드 목록이 반환되어야 한다") {
                    result shouldBe slice
                    verify { feedRepository.findAllByUserAndDeletedAtIsNull(user, pageable) }
                }
            }
        }

        Given("이전 피드 네비게이션을 위한 피드들이 있을 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val base = LocalDateTime.of(2025, 12, 22, 16, 0)
            val current = FeedTestFixture.createFeedWithCreateDate(100L, user, base)
            val first = FeedTestFixture.createFeedWithCreateDate(1L, user, base.plusMinutes(1))
            val second = FeedTestFixture.createFeedWithCreateDate(2L, user, base.plusMinutes(2))
            val third = FeedTestFixture.createFeedWithCreateDate(3L, user, base.plusMinutes(3))

            every { feedRepository.findPrevFeeds(base, null, PageRequest.of(0, 3)) } returns
                mutableListOf(first, second, third)

            When("이전 피드 2개를 요청하면") {
                val result = feedGetService.findPrevFeedsWithHasMore(current, emptyList(), 2)

                Then("요청한 개수만큼 반환하고 hasMore가 true여야 한다") {
                    result.hasMore shouldBe true
                    result.feeds shouldContainExactly listOf(second, first)
                    verify { feedRepository.findPrevFeeds(base, null, PageRequest.of(0, 3)) }
                }
            }
        }

        Given("다음 피드 네비게이션을 위한 피드들이 있을 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val blocked = UserTestFixture.createUser(2L, "blocked")
            val block =
                UserBlock
                    .builder()
                    .blocker(user)
                    .blocked(blocked)
                    .build()
            val base = LocalDateTime.of(2025, 12, 22, 16, 0)
            val current = FeedTestFixture.createFeedWithCreateDate(100L, user, base)
            val first = FeedTestFixture.createFeedWithCreateDate(1L, user, base.minusMinutes(1))
            val second = FeedTestFixture.createFeedWithCreateDate(2L, user, base.minusMinutes(2))
            val third = FeedTestFixture.createFeedWithCreateDate(3L, user, base.minusMinutes(3))

            every { feedRepository.findNextFeeds(base, listOf(2L), PageRequest.of(0, 3)) } returns
                mutableListOf(first, second, third)

            When("다음 피드 2개를 요청하면") {
                val result = feedGetService.findNextFeedsWithHasMore(current, listOf(block), 2)

                Then("요청한 개수만큼 반환하고 hasMore가 true여야 한다") {
                    result.hasMore shouldBe true
                    result.feeds shouldContainExactly listOf(first, second)
                    verify { feedRepository.findNextFeeds(base, listOf(2L), PageRequest.of(0, 3)) }
                }
            }
        }
    })
