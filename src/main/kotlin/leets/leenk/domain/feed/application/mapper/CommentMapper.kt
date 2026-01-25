package leets.leenk.domain.feed.application.mapper

import leets.leenk.domain.feed.application.dto.request.CommentWriteRequest
import leets.leenk.domain.feed.domain.entity.Comment
import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.user.domain.entity.User
import org.springframework.stereotype.Component

@Component
class CommentMapper {

    fun toComment(user: User, feed: Feed, request: CommentWriteRequest): Comment {
        return Comment(
            user = user,
            feed = feed,
            comment = request.comment,
        )
    }
}
