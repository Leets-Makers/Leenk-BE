package leets.leenk.domain.feed.test

import leets.leenk.domain.feed.domain.entity.Comment
import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.user.domain.entity.User

object CommentTestFixture {
    fun createComment(
        id: Long?,
        user: User,
        feed: Feed,
        text: String,
    ): Comment =
        Comment(
            commentId = id,
            user = user,
            feed = feed,
            comment = text,
        )
}
