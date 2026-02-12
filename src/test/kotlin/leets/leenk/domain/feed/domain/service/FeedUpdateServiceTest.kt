package leets.leenk.domain.feed.domain.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import leets.leenk.domain.feed.application.dto.request.FeedUpdateRequest
import leets.leenk.domain.feed.test.FeedTestFixture
import leets.leenk.domain.feed.test.ReactionTestFixture
import leets.leenk.domain.feed.test.UserTestFixture

class FeedUpdateServiceTest :
    BehaviorSpec({
        val feedUpdateService = FeedUpdateService()

        Given("피드와 업데이트 요청이 주어졌을 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val feed = FeedTestFixture.createFeed(1L, user)
            val request = FeedUpdateRequest("\n\nhello\n\n\nworld", null, null)

            When("피드를 업데이트하면") {
                feedUpdateService.update(feed, request)

                Then("description이 정리되어 업데이트되어야 한다") {
                    feed.description shouldBe "hello\nworld"
                }
            }
        }

        Given("기존 description이 있는 피드와 null description 요청이 주어졌을 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val feed = FeedTestFixture.createFeed(1L, user)
            feed.updateDescription("old")
            val request = FeedUpdateRequest(null, null, null)

            When("피드를 업데이트하면") {
                feedUpdateService.update(feed, request)

                Then("기존 description이 유지되어야 한다") {
                    feed.description shouldBe "old"
                }
            }
        }

        Given("피드, 리액션, 사용자가 주어졌을 때") {
            val user = UserTestFixture.createUser(1L, "me")
            val feed = FeedTestFixture.createFeed(1L, user)
            val reaction = ReactionTestFixture.createReaction(feed, user, 2L)

            When("리액션 수를 3으로 업데이트하면") {
                feedUpdateService.updateTotalReaction(feed, reaction, user, 3L)

                Then("피드, 리액션, 사용자의 리액션 수가 증가해야 한다") {
                    feed.totalReactionCount shouldBe 3L
                    reaction.reactionCount shouldBe 5L
                    user.totalReactionCount shouldBe 3L
                }
            }
        }
    })
