package leets.leenk.domain.feed.domain.repository

import leets.leenk.domain.feed.domain.entity.Comment
import leets.leenk.domain.feed.domain.entity.Feed
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByCommentIdAndDeletedAtIsNull(id: Long): Optional<Comment>

    fun findAllByFeedAndDeletedAtIsNullOrderByCreateDateDesc(feed: Feed): List<Comment>
}
