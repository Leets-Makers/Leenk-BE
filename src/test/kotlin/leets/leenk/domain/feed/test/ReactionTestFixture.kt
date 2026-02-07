package leets.leenk.domain.feed.test

import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.entity.Reaction
import leets.leenk.domain.user.domain.entity.User

object ReactionTestFixture {
    fun createReaction(
        feed: Feed,
        user: User,
        count: Long,
    ): Reaction =
        Reaction(
            feed = feed,
            user = user,
            reactionCount = count,
        )
}
