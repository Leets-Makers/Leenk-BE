package leets.leenk.domain.feed.test.fixture

import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.user.domain.entity.User

class FeedTestFixture {
    companion object {
        fun createFeed(
            user: User,
            description: String = "테스트 피드",
            totalReactionCount: Long = 0L,
        ): Feed =
            Feed
                .builder()
                .user(user)
                .description(description)
                .totalReactionCount(totalReactionCount)
                .build()
    }
}
