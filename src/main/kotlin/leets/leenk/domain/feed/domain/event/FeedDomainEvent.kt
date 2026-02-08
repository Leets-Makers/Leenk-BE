package leets.leenk.domain.feed.domain.event

/**
 * Feed 도메인의 단일 이벤트
 * eventType으로 이벤트 종류 구분, data로 필요한 정보 전달
 */
data class FeedDomainEvent(
    val eventType: FeedEventType,
    val data: Map<String, Any>
) {
    companion object {
        /**
         * 피드 생성 이벤트
         */
        fun created(
            feedId: Long,
            authorId: Long,
            authorName: String,
            taggedUserIds: List<Long> = emptyList()
        ) = FeedDomainEvent(
            eventType = FeedEventType.CREATED,
            data = mapOf(
                "feedId" to feedId,
                "authorId" to authorId,
                "authorName" to authorName,
                "taggedUserIds" to taggedUserIds
            )
        )

        /**
         * 피드 공감 이벤트
         */
        fun reacted(
            feedId: Long,
            feedAuthorId: Long,
            reactorId: Long,
            reactorName: String,
            totalReactionCount: Int
        ) = FeedDomainEvent(
            eventType = FeedEventType.REACTED,
            data = mapOf(
                "feedId" to feedId,
                "feedAuthorId" to feedAuthorId,
                "reactorId" to reactorId,
                "reactorName" to reactorName,
                "totalReactionCount" to totalReactionCount
            )
        )
    }

    // 타입 안전한 접근자
    val feedId: Long get() = data["feedId"] as Long
    val authorId: Long get() = data["authorId"] as Long
    val authorName: String get() = data["authorName"] as String
    val taggedUserIds: List<Long>
        get() = (data["taggedUserIds"] as? List<*>)?.filterIsInstance<Long>() ?: emptyList()
    val feedAuthorId: Long get() = data["feedAuthorId"] as Long
    val reactorId: Long get() = data["reactorId"] as Long
    val reactorName: String get() = data["reactorName"] as String
    val totalReactionCount: Int get() = data["totalReactionCount"] as Int
}
