package leets.leenk.domain.feed.domain.event

sealed class FeedDomainEvent {
    abstract val feedId: Long

    data class Created(
        override val feedId: Long,
        val authorId: Long,
        val authorName: String,
        val taggedUserIds: List<Long> = emptyList(),
    ) : FeedDomainEvent()

    data class Reacted(
        override val feedId: Long,
        val feedAuthorId: Long,
        val reactorId: Long,
        val reactorName: String,
        val previousReactionCount: Long,
        val totalReactionCount: Long,
    ) : FeedDomainEvent()
}
