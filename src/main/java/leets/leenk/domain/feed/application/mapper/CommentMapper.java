package leets.leenk.domain.feed.application.mapper;

import leets.leenk.domain.feed.application.dto.request.CommentWriteRequest;
import leets.leenk.domain.feed.domain.entity.Comment;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public Comment toComment(User user, Feed feed, CommentWriteRequest request) {
        return Comment.builder()
                .user(user)
                .feed(feed)
                .comment(request.comment())
                .build();
    }
}
