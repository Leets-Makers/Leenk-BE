package leets.leenk.domain.feed.domain.service;

import leets.leenk.domain.feed.domain.entity.Comment;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.repository.CommentRepository;
import leets.leenk.domain.feed.test.CommentTestFixture;
import leets.leenk.domain.feed.test.FeedTestFixture;
import leets.leenk.domain.feed.test.UserTestFixture;
import leets.leenk.domain.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class CommentSaveServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentSaveService commentSaveService;

    @Test
    @DisplayName("saveComment 테스트")
    void saveComment() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        Feed feed = FeedTestFixture.createFeed(1L, user);
        Comment comment = CommentTestFixture.createComment(null, user, feed, "hi");

        // when
        commentSaveService.saveComment(comment);

        // then
        then(commentRepository).should().save(comment);
    }
}
