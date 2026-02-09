package leets.leenk.domain.feed.domain.service

import leets.leenk.domain.feed.application.dto.request.FeedUpdateRequest
import leets.leenk.domain.feed.application.util.FeedDescriptionUtil.normalizeDescription
import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.entity.Reaction
import leets.leenk.domain.user.domain.entity.User
import org.springframework.stereotype.Service

@Service
class FeedUpdateService {
    fun update(
        feed: Feed,
        request: FeedUpdateRequest,
    ) {
        val normalized = normalizeDescription(request.description)
        normalized?.let { feed.updateDescription(it) }
    }

    fun updateTotalReaction(
        feed: Feed,
        reaction: Reaction,
        user: User,
        reactionCount: Long,
    ) {
        feed.increaseTotalReactionCount(reactionCount)
        reaction.increaseReactionCount(reactionCount)
        user.increaseTotalReactionCount(reactionCount)
    }
}
