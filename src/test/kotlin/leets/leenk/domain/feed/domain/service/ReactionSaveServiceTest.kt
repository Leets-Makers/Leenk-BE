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

class ReactionSaveServiceTest :
    BehaviorSpec({
        val reactionRepository = mockk<ReactionRepository>()
        val reactionSaveService = ReactionSaveService(reactionRepository)

        Given("리액션이 주어졌을 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val feed = FeedTestFixture.createFeed(1L, user)
            val reaction = ReactionTestFixture.createReaction(feed, user, 1L)

            every { reactionRepository.save(reaction) } returns reaction

            When("리액션을 저장하면") {
                val result = reactionSaveService.save(reaction)

                Then("저장된 리액션이 반환되어야 한다") {
                    result shouldBe reaction
                    verify { reactionRepository.save(reaction) }
                }
            }
        }
    })
