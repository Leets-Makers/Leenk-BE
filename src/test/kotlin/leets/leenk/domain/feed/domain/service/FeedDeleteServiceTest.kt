package leets.leenk.domain.feed.domain.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import leets.leenk.domain.feed.test.FeedTestFixture
import leets.leenk.domain.feed.test.UserTestFixture
import java.time.LocalDateTime

class FeedDeleteServiceTest :
    BehaviorSpec({
        val feedDeleteService = FeedDeleteService()

        Given("사용자와 피드가 주어졌을 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val feed = FeedTestFixture.createFeed(1L, user)

            When("피드를 삭제하면") {
                val start = LocalDateTime.now()
                feedDeleteService.delete(feed)
                val end = LocalDateTime.now()

                Then("deletedAt이 현재 시간으로 설정되어야 한다") {
                    feed.deletedAt shouldNotBe null
                    (feed.deletedAt!! >= start) shouldBe true
                    (feed.deletedAt!! <= end) shouldBe true
                }
            }
        }
    })
