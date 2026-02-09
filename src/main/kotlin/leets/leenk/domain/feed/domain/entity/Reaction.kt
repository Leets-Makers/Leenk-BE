package leets.leenk.domain.feed.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import leets.leenk.domain.user.domain.entity.User

@Entity
@Table(
    name = "reactions",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["feed_id", "user_id"]),
    ],
)
class Reaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var reactionCount: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "feed_id", nullable = false, updatable = false)
    val feed: Feed,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    val user: User,
) {
    fun increaseReactionCount(reactionCount: Long) {
        this.reactionCount += reactionCount
    }
}
