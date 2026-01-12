package leets.leenk.domain.feed.domain.repository;

import jakarta.persistence.EntityManager;
import leets.leenk.config.MysqlTestConfig;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.Reaction;
import leets.leenk.domain.feed.test.FeedTestFixture;
import leets.leenk.domain.feed.test.ReactionTestFixture;
import leets.leenk.domain.feed.test.UserTestFixture;
import leets.leenk.domain.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(MysqlTestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReactionRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private ReactionRepository reactionRepository;

    @Test
    @DisplayName("findByFeedAndUser 테스트")
    void findByFeedAndUser() {
        // given
        User author = persistUser(1L, "author");
        User me = persistUser(2L, "me");

        Feed feed = persistFeed(author);

        Reaction r1 = reactionRepository.save(ReactionTestFixture.createReaction(feed, me, 7L));

        flushAndClear();

        // when
        Reaction result = reactionRepository.findByFeedAndUser(feed, me).orElseThrow();

        // then
        assertThat(result.getId()).isEqualTo(r1.getId());
        assertThat(result.getReactionCount()).isEqualTo(7L);
    }

    @Test
    @DisplayName("findAllByFeed 테스트")
    void findAllByFeed() {
        // given
        User author = persistUser(1L, "author");
        User u1 = persistUser(2L, "u1");
        User u2 = persistUser(3L, "u2");
        User u3 = persistUser(4L, "u3");

        Feed feed = persistFeed(author);

        reactionRepository.save(ReactionTestFixture.createReaction(feed, u1, 7L));
        reactionRepository.save(ReactionTestFixture.createReaction(feed, u2, 7L));
        reactionRepository.save(ReactionTestFixture.createReaction(feed, u3, 7L));

        flushAndClear();

        // when
        List<Reaction> result = reactionRepository.findAllByFeed(feed);

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.stream().map(Reaction::getReactionCount).toList())
                .containsExactly(7L, 7L, 7L);
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    private User persistUser(Long id, String name) {
        User user = UserTestFixture.createUser(id, name);
        em.persist(user);
        return user;
    }

    private Feed persistFeed(User user) {
        Feed feed = FeedTestFixture.createFeed(null, user);
        em.persist(feed);
        return feed;
    }
}
