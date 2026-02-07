package leets.leenk.domain.feed.domain.repository

import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.entity.Reaction
import leets.leenk.domain.user.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface ReactionRepository : JpaRepository<Reaction, Long> {
    fun findByFeedAndUser(
        feed: Feed,
        user: User,
    ): Optional<Reaction>

    @Query("SELECT r FROM Reaction r JOIN FETCH r.user WHERE r.feed = :feed order by r.reactionCount desc")
    fun findAllByFeed(feed: Feed): List<Reaction>
}
