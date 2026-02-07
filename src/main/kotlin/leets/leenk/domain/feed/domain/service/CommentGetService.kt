package leets.leenk.domain.feed.domain.service

import leets.leenk.domain.feed.application.exception.CommentNotFoundException
import leets.leenk.domain.feed.domain.entity.Comment
import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.repository.CommentRepository
import org.springframework.stereotype.Service

@Service
class CommentGetService(
    private val commentRepository: CommentRepository,
) {
    fun findCommentByIdNotDeleted(commentId: Long): Comment =
        commentRepository
            .findByCommentIdAndDeletedAtIsNull(commentId)
            .orElseThrow { CommentNotFoundException() }

    fun findAllByFeed(feed: Feed): List<Comment> =
        commentRepository.findAllByFeedAndDeletedAtIsNullOrderByCreateDateDesc(feed)
}
