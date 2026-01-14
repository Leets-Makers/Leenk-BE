package leets.leenk.domain.feed.domain.service;

import leets.leenk.domain.feed.domain.entity.Comment;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.test.CommentTestFixture;
import leets.leenk.domain.feed.test.FeedTestFixture;
import leets.leenk.domain.feed.test.UserTestFixture;
import leets.leenk.domain.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentDeleteServiceTest {
    private final CommentDeleteService commentDeleteService = new CommentDeleteService();

    @Test
    @DisplayName("CommentDeleteService 테스트")
    void CommentDeleteService() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        Feed feed = FeedTestFixture.createFeed(1L, user);
        Comment comment = CommentTestFixture.createComment(null, user, feed, "hi");

        // when
        LocalDateTime start = LocalDateTime.now();

        commentDeleteService.deleteComment(comment);

        LocalDateTime end = LocalDateTime.now();

        // then
        assertThat(comment.getDeletedAt()).isNotNull();
        assertThat(comment.getDeletedAt()).isAfterOrEqualTo(start);
        assertThat(comment.getDeletedAt()).isBeforeOrEqualTo(end);
    }
}
