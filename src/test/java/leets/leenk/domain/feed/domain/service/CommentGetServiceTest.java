package leets.leenk.domain.feed.domain.service;

import leets.leenk.domain.feed.application.exception.CommentNotFoundException;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class CommentGetServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentGetService commentGetService;

    @Test
    @DisplayName("findCommentByIdNotDeleted 테스트")
    void findCommentByIdNotDeleted1() {
        //given
        User user = UserTestFixture.createUser(1L, "me");
        Feed feed = FeedTestFixture.createFeed(1L, user);
        Comment comment = CommentTestFixture.createComment(12L, user, feed, "hi");

        given(commentRepository.findByCommentIdAndDeletedAtIsNull(12L))
                .willReturn(Optional.of(comment));

        // when
        Comment result = commentGetService.findCommentByIdNotDeleted(12L);

        // then
        assertThat(result).isSameAs(comment);
        then(commentRepository).should().findByCommentIdAndDeletedAtIsNull(12L);
    }

    @Test
    @DisplayName("findCommentByIdNotDeleted 테스트 - 예외")
    void findCommentByIdNotDeleted2() {
        // given
        given(commentRepository.findByCommentIdAndDeletedAtIsNull(99L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentGetService.findCommentByIdNotDeleted(99L))
                .isInstanceOf(CommentNotFoundException.class);
        then(commentRepository).should().findByCommentIdAndDeletedAtIsNull(99L);
    }

    @Test
    @DisplayName("findAllByFeed 테스트")
    void findAllByFeed() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        Feed feed = FeedTestFixture.createFeed(1L, user);
        Comment first = CommentTestFixture.createComment(1L, user, feed, "first");
        Comment second = CommentTestFixture.createComment(2L, user, feed, "second");

        given(commentRepository.findAllByFeedAndDeletedAtIsNullOrderByCreateDateDesc(feed))
                .willReturn(List.of(first, second));

        // when
        List<Comment> result = commentGetService.findAllByFeed(feed);

        // then
        assertThat(result).containsExactly(first, second);
        then(commentRepository).should().findAllByFeedAndDeletedAtIsNullOrderByCreateDateDesc(feed);
    }
}
