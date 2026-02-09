package leets.leenk.domain.feed.domain.service

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.feed.domain.repository.LinkedUserRepository
import leets.leenk.domain.feed.test.FeedTestFixture
import leets.leenk.domain.feed.test.LinkedUserTestFixture
import leets.leenk.domain.feed.test.UserTestFixture

class LinkedUserSaveServiceTest :
    BehaviorSpec({
        val linkedUserRepository = mockk<LinkedUserRepository>(relaxed = true)
        val linkedUserSaveService = LinkedUserSaveService(linkedUserRepository)

        Given("연결된 사용자가 주어졌을 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val feed = FeedTestFixture.createFeed(1L, user)
            val linkedUser = LinkedUserTestFixture.createLinkedUser(1L, user, feed)

            every { linkedUserRepository.save(any()) } returns linkedUser

            When("연결된 사용자를 저장하면") {
                linkedUserSaveService.save(linkedUser)

                Then("linkedUserRepository의 save 메서드가 호출되어야 한다") {
                    verify { linkedUserRepository.save(linkedUser) }
                }
            }
        }

        Given("연결된 사용자 목록이 주어졌을 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val feed = FeedTestFixture.createFeed(1L, user)
            val linkedUser = LinkedUserTestFixture.createLinkedUser(1L, user, feed)
            val linkedUsers = listOf(linkedUser)

            When("연결된 사용자 목록을 저장하면") {
                linkedUserSaveService.saveAll(linkedUsers)

                Then("linkedUserRepository의 saveAll 메서드가 호출되어야 한다") {
                    verify { linkedUserRepository.saveAll(linkedUsers) }
                }
            }
        }
    })
