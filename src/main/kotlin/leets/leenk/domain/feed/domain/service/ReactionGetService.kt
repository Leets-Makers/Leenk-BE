package leets.leenk.domain.feed.domain.service

import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.entity.Reaction
import leets.leenk.domain.feed.domain.repository.ReactionRepository
import leets.leenk.domain.user.domain.entity.User
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class ReactionGetService(
    private val reactionRepository: ReactionRepository,
) {

    fun findByFeedAndUser(feed: Feed, user: User): Optional<Reaction> {
        return reactionRepository.findByFeedAndUser(feed, user)
    }

    fun findAll(feed: Feed): List<Reaction> {
        return reactionRepository.findAllByFeed(feed)
    }
}
