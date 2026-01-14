package leets.leenk.domain.feed.domain.repository;

import jakarta.persistence.EntityManager;
import leets.leenk.config.MysqlTestConfig;
import leets.leenk.domain.feed.domain.entity.Comment;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.test.CommentTestFixture;
import leets.leenk.domain.feed.test.FeedTestFixture;
import leets.leenk.domain.feed.test.UserTestFixture;
import leets.leenk.domain.user.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
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

    private static final LocalDateTime BASE_TIME = LocalDateTime.of(2025, 12, 22, 16, 0);
    private User author;

    @BeforeEach
    void setUp() {
        author = persistUser();
    }

    @Test
    @DisplayName("findByCommentIdAndDeletedAtIsNull 테스트")
    void findByCommentIdAndDeletedAtIsNull() {
        // given
        Feed feed = persistFeed(author);

        Comment notDeleted = commentRepository.save(CommentTestFixture.createComment(null, author, feed, "hi1"));
        Comment deleted = commentRepository.save(CommentTestFixture.createComment(null, author, feed, "hi2"));

        flushAndClear();

        updateCommentDates(notDeleted.getCommentId(), BASE_TIME.plusMinutes(1), null);
        updateCommentDates(deleted.getCommentId(), BASE_TIME.plusMinutes(2), BASE_TIME.plusMinutes(1));

        flushAndClear();

        // when
        Optional<Comment> notDeletedComment = commentRepository.findByCommentIdAndDeletedAtIsNull(notDeleted.getCommentId());
        Optional<Comment> deletedComment = commentRepository.findByCommentIdAndDeletedAtIsNull(deleted.getCommentId());

        // then
        assertThat(notDeletedComment).isPresent();
        assertThat(notDeletedComment.get().getComment()).isEqualTo(notDeleted.getComment());
        assertThat(deletedComment).isEmpty();
    }

    @Test
    @DisplayName("findAllByFeedAndDeletedAtIsNullOrderByCreateDateDesc 테스트")
    void findAllByFeedAndDeletedAtIsNullOrderByCreateDateDesc() {
        // given
        Feed feed1 = persistFeed(author);
        Feed feed2 = persistFeed(author);

        Comment c1 = commentRepository.save(CommentTestFixture.createComment(null, author, feed1, "hi1"));
        Comment c2 = commentRepository.save(CommentTestFixture.createComment(null, author, feed1, "hi2"));
        Comment c3Deleted = commentRepository.save(CommentTestFixture.createComment(null, author, feed1, "hi3"));
        Comment other = commentRepository.save(CommentTestFixture.createComment(null, author, feed2, "other"));

        flushAndClear();

        updateCommentDates(c1.getCommentId(), BASE_TIME.plusMinutes(1), null);
        updateCommentDates(c2.getCommentId(), BASE_TIME.plusMinutes(2), null);
        updateCommentDates(c3Deleted.getCommentId(), BASE_TIME.plusMinutes(3), BASE_TIME.plusMinutes(3));
        updateCommentDates(other.getCommentId(), BASE_TIME.plusMinutes(4), null);

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

    private User persistUser() {
        User user = UserTestFixture.createUser(1L, "me");
        em.persist(user);
        return user;
    }

    private Feed persistFeed(User user) {
        Feed feed = FeedTestFixture.createFeed(null, user);
        em.persist(feed);
        return feed;
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
