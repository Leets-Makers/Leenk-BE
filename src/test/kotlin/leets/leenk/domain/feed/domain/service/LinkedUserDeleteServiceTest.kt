package leets.leenk.domain.feed.domain.service

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.feed.domain.repository.LinkedUserRepository
import leets.leenk.domain.feed.test.FeedTestFixture
import leets.leenk.domain.feed.test.UserTestFixture

class LinkedUserDeleteServiceTest :
    BehaviorSpec({
        val linkedUserRepository = mockk<LinkedUserRepository>(relaxed = true)
        val linkedUserDeleteService = LinkedUserDeleteService(linkedUserRepository)

        Given("피드가 주어졌을 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val feed = FeedTestFixture.createFeed(1L, user)

            When("해당 피드의 모든 연결된 사용자를 삭제하면") {
                linkedUserDeleteService.deleteAllByFeed(feed)

                Then("linkedUserRepository의 deleteAllByFeed와 flush가 호출되어야 한다") {
                    verify { linkedUserRepository.deleteAllByFeed(feed) }
                    verify { linkedUserRepository.flush() }
                }
            }
        }
    })
