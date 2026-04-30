package leets.leenk.domain.feed.domain.event

object FeedDomainEventFixture {
    fun created(
        feedId: Long = 100L,
        authorId: Long = 1L,
        authorName: String = "홍길동",
        taggedUserIds: List<Long> = emptyList(),
    ) = FeedDomainEvent.Created(
        feedId = feedId,
        authorId = authorId,
        authorName = authorName,
        taggedUserIds = taggedUserIds,
    )

    fun reacted(
        feedId: Long = 100L,
        feedAuthorId: Long = 1L,
        reactorId: Long = 2L,
        reactorName: String = "김철수",
        previousReactionCount: Long = 0L,
        totalReactionCount: Long = 1L,
    ) = FeedDomainEvent.Reacted(
        feedId = feedId,
        feedAuthorId = feedAuthorId,
        reactorId = reactorId,
        reactorName = reactorName,
        previousReactionCount = previousReactionCount,
        totalReactionCount = totalReactionCount,
    )
}
