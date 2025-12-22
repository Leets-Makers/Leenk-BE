package leets.leenk.domain.feed.domain.repository;

import jakarta.persistence.EntityManager;
import leets.leenk.config.MysqlTestConfig;
import leets.leenk.domain.feed.domain.entity.Comment;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.test.CommentTestFixture;
import leets.leenk.domain.feed.test.FeedTestFixture;
import leets.leenk.domain.feed.test.UserTestFixture;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(MysqlTestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTest {
    @Autowired
    EntityManager em;

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FeedRepository feedRepository;

    @Test
    @DisplayName("findByCommentIdAndDeletedAtIsNull 테스트")
    void findByCommentIdAndDeletedAtIsNull() {
        // given
        User author = userRepository.save(UserTestFixture.createUser(1L, "me"));
        Feed feed = feedRepository.save(FeedTestFixture.createFeed(null, author));

        Comment notDeleted = commentRepository.save(CommentTestFixture.createComment(null, author, feed, "hi1"));
        Comment deleted = commentRepository.save(CommentTestFixture.createComment(null, author, feed, "hi2"));

        flushAndClear();

        LocalDateTime base = LocalDateTime.of(2025, 12, 22, 16, 0);

        updateCommentDates(notDeleted.getCommentId(), base.plusMinutes(1), null);
        updateCommentDates(deleted.getCommentId(), base.plusMinutes(2), base.plusMinutes(1));

        flushAndClear();

        // when
        Optional<Comment> notDeletedComment = commentRepository.findByCommentIdAndDeletedAtIsNull(notDeleted.getCommentId());
        Optional<Comment> deletedComment = commentRepository.findByCommentIdAndDeletedAtIsNull(deleted.getCommentId());

        // then
        assertThat(notDeletedComment).isPresent();
        assertThat(notDeletedComment.get().getComment()).isEqualTo("hi1");
        assertThat(deletedComment).isEmpty();
    }

    @Test
    @DisplayName("findAllByFeedAndDeletedAtIsNullOrderByCreateDateDesc 테스트")
    void findAllByFeedAndDeletedAtIsNullOrderByCreateDateDesc() {
        // given
        User author = userRepository.save(UserTestFixture.createUser(1L, "me"));
        Feed feed1 = feedRepository.save(FeedTestFixture.createFeed(null, author));
        Feed feed2 = feedRepository.save(FeedTestFixture.createFeed(null, author));

        Comment c1 = commentRepository.save(CommentTestFixture.createComment(null, author, feed1, "hi1"));
        Comment c2 = commentRepository.save(CommentTestFixture.createComment(null, author, feed1, "hi2"));
        Comment c3Deleted = commentRepository.save(CommentTestFixture.createComment(null, author, feed1, "hi3"));
        Comment other = commentRepository.save(CommentTestFixture.createComment(null, author, feed2, "other"));

        flushAndClear();

        LocalDateTime base = LocalDateTime.of(2025, 12, 22, 16, 0);

        updateCommentDates(c1.getCommentId(), base.plusMinutes(1), null);
        updateCommentDates(c2.getCommentId(), base.plusMinutes(2), null);
        updateCommentDates(c3Deleted.getCommentId(), base.plusMinutes(3), base.plusMinutes(3));
        updateCommentDates(other.getCommentId(), base.plusMinutes(4), null);

        flushAndClear();

        // when
        List<Comment> result = commentRepository.findAllByFeedAndDeletedAtIsNullOrderByCreateDateDesc(feed1);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.stream().map(Comment::getCommentId).toList())
                .containsExactly(c2.getCommentId(), c1.getCommentId());
        assertThat(result).allSatisfy(comment -> {
            assertThat(comment.getFeed().getId()).isEqualTo(feed1.getId());
            assertThat(comment.getDeletedAt()).isNull();
        });
    }

    private void updateCommentDates(Long commentId, LocalDateTime createdDate, LocalDateTime deletedAt) {
        em.createQuery("UPDATE Comment c SET c.createDate = :createDate, c.deletedAt = :deletedAt WHERE c.commentId = :id")
                .setParameter("createDate", createdDate)
                .setParameter("deletedAt", deletedAt)
                .setParameter("id", commentId)
                .executeUpdate();
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
