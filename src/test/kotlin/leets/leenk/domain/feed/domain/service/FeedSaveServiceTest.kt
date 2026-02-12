package leets.leenk.domain.feed.domain.service

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.feed.domain.repository.FeedRepository
import leets.leenk.domain.feed.test.FeedTestFixture
import leets.leenk.domain.feed.test.UserTestFixture

class FeedSaveServiceTest :
    BehaviorSpec({
        val feedRepository = mockk<FeedRepository>(relaxed = true)
        val feedSaveService = FeedSaveService(feedRepository)

        Given("사용자와 피드가 주어졌을 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val feed = FeedTestFixture.createFeed(1L, user)

            every { feedRepository.save(any()) } returns feed

            When("피드를 저장하면") {
                feedSaveService.save(feed)

                Then("feedRepository의 save 메서드가 호출되어야 한다") {
                    verify { feedRepository.save(feed) }
                }
            }
        }
    })
