package leets.leenk.domain.feed.test

import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.user.domain.entity.User
import leets.leenk.global.common.entity.BaseEntity
import java.time.LocalDateTime

object FeedTestFixture {
    fun createFeed(
        user: User,
        description: String = "테스트 피드",
        totalReactionCount: Long = 0L,
    ): Feed =
        Feed(
            user = user,
            description = description,
            totalReactionCount = totalReactionCount,
        )

    fun createFeed(
        id: Long?,
        author: User,
    ): Feed =
        Feed(
            id = id,
            user = author,
            description = "desc",
            totalReactionCount = 0L,
        )

    fun createFeedWithCreateDate(
        id: Long,
        user: User,
        createDate: LocalDateTime,
    ): Feed {
        val feed =
            Feed(
                id = id,
                user = user,
                description = "desc",
                totalReactionCount = 0L,
            )

        val createDateField = BaseEntity::class.java.getDeclaredField("createDate")
        createDateField.isAccessible = true
        createDateField.set(feed, createDate)

        return feed
    }
}
