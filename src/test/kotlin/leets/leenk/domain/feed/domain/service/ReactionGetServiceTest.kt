package leets.leenk.domain.feed.domain.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.feed.domain.repository.ReactionRepository
import leets.leenk.domain.feed.test.FeedTestFixture
import leets.leenk.domain.feed.test.ReactionTestFixture
import leets.leenk.domain.feed.test.UserTestFixture
import java.util.Optional

class ReactionGetServiceTest :
    BehaviorSpec({
        val reactionRepository = mockk<ReactionRepository>()
        val reactionGetService = ReactionGetService(reactionRepository)

        Given("피드와 사용자에 대한 리액션이 존재할 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val feed = FeedTestFixture.createFeed(1L, user)
            val reaction = ReactionTestFixture.createReaction(feed, user, 1L)

            every { reactionRepository.findByFeedAndUser(feed, user) } returns Optional.of(reaction)

            When("피드와 사용자로 리액션을 조회하면") {
                val result = reactionGetService.findByFeedAndUser(feed, user)

                Then("해당 리액션이 반환되어야 한다") {
                    result.get() shouldBe reaction
                    verify { reactionRepository.findByFeedAndUser(feed, user) }
                }
            }
        }

        Given("피드에 여러 리액션이 존재할 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val feed = FeedTestFixture.createFeed(1L, user)
            val reaction = ReactionTestFixture.createReaction(feed, user, 2L)
            val reactions = listOf(reaction)

            every { reactionRepository.findAllByFeed(feed) } returns reactions

            When("피드의 모든 리액션을 조회하면") {
                val result = reactionGetService.findAll(feed)

                Then("리액션 목록이 반환되어야 한다") {
                    result shouldBe reactions
                    verify { reactionRepository.findAllByFeed(feed) }
                }
            }
        }
    })
