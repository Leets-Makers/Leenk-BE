package leets.leenk.domain.feed.application.mapper

import leets.leenk.domain.feed.application.dto.response.FeedUserListResponse
import leets.leenk.domain.feed.application.dto.response.FeedUserResponse
import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.entity.LinkedUser
import leets.leenk.domain.user.application.mapper.UserProfileMapper
import leets.leenk.domain.user.domain.entity.User
import leets.leenk.global.common.dto.PageableMapperUtil
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component

@Component
class FeedUserMapper(
    private val userProfileMapper: UserProfileMapper,
) {
    fun toLinkedUser(
        user: User,
        feed: Feed,
    ): LinkedUser =
        LinkedUser(
            feed = feed,
            user = user,
        )

    fun toFeedUserResponse(user: User): FeedUserResponse =
        FeedUserResponse(
            user = userProfileMapper.toProfile(user),
        )

    fun toFeedUserListResponse(slice: Slice<User>): FeedUserListResponse {
        val responses =
            slice.content
                .map { toFeedUserResponse(it) }

        return FeedUserListResponse(
            users = responses,
            pageable = PageableMapperUtil.from(slice),
        )
    }
}
