package leets.leenk.domain.feed.test;

import leets.leenk.domain.feed.domain.entity.Comment;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.user.domain.entity.User;

public class CommentTestFixture {
    public static Comment createComment(Long id, User user, Feed feed, String text) {
        return Comment.builder()
                .commentId(id)
                .user(user)
                .feed(feed)
                .comment(text)
                .build();
    }
}
