package leets.leenk.domain.feed.test

import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.entity.LinkedUser
import leets.leenk.domain.user.domain.entity.User

object LinkedUserTestFixture {
    fun createLinkedUser(
        id: Long?,
        user: User,
        feed: Feed,
    ): LinkedUser =
        LinkedUser(
            id = id,
            feed = feed,
            user = user,
        )
}
