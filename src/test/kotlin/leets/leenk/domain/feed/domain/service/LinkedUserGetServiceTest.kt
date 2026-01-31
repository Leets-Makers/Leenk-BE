package leets.leenk.domain.feed.domain.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.repository.LinkedUserRepository
import leets.leenk.domain.feed.test.FeedTestFixture
import leets.leenk.domain.feed.test.LinkedUserTestFixture
import leets.leenk.domain.feed.test.UserTestFixture
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.SliceImpl

class LinkedUserGetServiceTest :
    BehaviorSpec({
        val linkedUserRepository = mockk<LinkedUserRepository>()
        val linkedUserGetService = LinkedUserGetService(linkedUserRepository)

        Given("피드에 연결된 사용자들이 있을 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val feed = FeedTestFixture.createFeed(1L, user)
            val linkedUser = LinkedUserTestFixture.createLinkedUser(1L, user, feed)
            val linkedUsers = listOf(linkedUser)

            every { linkedUserRepository.findAllByFeed(feed) } returns linkedUsers

            When("피드의 모든 연결된 사용자를 조회하면") {
                val result = linkedUserGetService.findAll(feed)

                Then("연결된 사용자 목록이 반환되어야 한다") {
                    result shouldBe linkedUsers
                    verify { linkedUserRepository.findAllByFeed(feed) }
                }
            }
        }

        Given("사용자가 연결된 피드들이 있을 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val pageable = PageRequest.of(0, 5)
            val slice = SliceImpl<Feed>(emptyList())

            every { linkedUserRepository.findFeedsByLinkedUser(user, pageable) } returns slice

            When("사용자가 연결된 모든 피드를 조회하면") {
                val result = linkedUserGetService.findAllByUser(user, pageable)

                Then("연결된 피드 목록이 반환되어야 한다") {
                    result shouldBe slice
                    verify { linkedUserRepository.findFeedsByLinkedUser(user, pageable) }
                }
            }
        }
    })
