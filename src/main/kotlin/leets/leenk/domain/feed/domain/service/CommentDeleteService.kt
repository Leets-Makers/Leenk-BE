package leets.leenk.domain.feed.domain.service

import leets.leenk.domain.feed.domain.entity.Comment
import org.springframework.stereotype.Service

@Service
class CommentDeleteService {
    fun deleteComment(comment: Comment) {
        comment.deleteComment()
    }
}
