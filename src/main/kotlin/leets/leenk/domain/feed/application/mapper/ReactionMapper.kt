package leets.leenk.domain.feed.application.mapper

import leets.leenk.domain.feed.application.dto.response.ReactionUserResponse
import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.entity.Reaction
import leets.leenk.domain.user.application.mapper.UserProfileMapper
import leets.leenk.domain.user.domain.entity.User
import org.springframework.stereotype.Component

@Component
class ReactionMapper(
    private val userProfileMapper: UserProfileMapper,
) {
    fun toResponse(reaction: Reaction): ReactionUserResponse =
        ReactionUserResponse(
            user = userProfileMapper.toProfile(reaction.user),
            reactionCount = reaction.reactionCount,
        )

    fun toReaction(
        user: User,
        feed: Feed,
        reactionCount: Long,
    ): Reaction =
        Reaction(
            user = user,
            feed = feed,
            reactionCount = reactionCount,
        )
}
