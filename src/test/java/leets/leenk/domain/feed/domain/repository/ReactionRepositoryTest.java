package leets.leenk.domain.feed.domain.repository;

import jakarta.persistence.EntityManager;
import leets.leenk.config.MysqlTestConfig;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.Reaction;
import leets.leenk.domain.feed.test.FeedTestFixture;
import leets.leenk.domain.feed.test.ReactionTestFixture;
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
    @Autowired
    private FeedRepository feedRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findByFeedAndUser 테스트")
    void findByFeedAndUser() {
        // given
        User author = userRepository.save(UserTestFixture.createUser(1L, "author"));
        User me = userRepository.save(UserTestFixture.createUser(2L, "me"));

        Feed feed = feedRepository.save(FeedTestFixture.createFeed(null, author));

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
        User author = userRepository.save(UserTestFixture.createUser(1L, "author"));
        User u1 = userRepository.save(UserTestFixture.createUser(2L, "u1"));
        User u2 = userRepository.save(UserTestFixture.createUser(3L, "u2"));
        User u3 = userRepository.save(UserTestFixture.createUser(4L, "u3"));

        Feed feed = feedRepository.save(FeedTestFixture.createFeed(null, author));

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
}
