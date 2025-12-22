package leets.leenk.domain.feed.domain.repository;

import jakarta.persistence.EntityManager;
import leets.leenk.config.MysqlTestConfig;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.LinkedUser;
import leets.leenk.domain.feed.test.FeedTestFixture;
import leets.leenk.domain.feed.test.LinkedUserTextFixture;
import leets.leenk.domain.feed.test.UserTestFixture;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(MysqlTestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LinkedUserRepositoryTest {
    @Autowired
    EntityManager em;
    @Autowired
    UserRepository userRepository;
    @Autowired
    LinkedUserRepository linkedUserRepository;
    @Autowired
    FeedRepository feedRepository;

    @Test
    @DisplayName("findAllByFeed 테스트")
    void findAllByFeed() {
        // given
        User author = userRepository.save(UserTestFixture.createUser(1L, "author"));
        User u1 = userRepository.save(UserTestFixture.createUser(2L, "u1"));
        User u2 = userRepository.save(UserTestFixture.createUser(3L, "u2"));
        User u3 = userRepository.save(UserTestFixture.createUser(4L, "u3"));

        Feed feed = feedRepository.save(FeedTestFixture.createFeed(null, author));

        LinkedUser l1 = linkedUserRepository.save(LinkedUserTextFixture.createLinkedUser(null, u1, feed));
        LinkedUser l2 = linkedUserRepository.save(LinkedUserTextFixture.createLinkedUser(null, u2, feed));
        LinkedUser l3 = linkedUserRepository.save(LinkedUserTextFixture.createLinkedUser(null, u3, feed));

        flushAndClear();

        // when
        List<LinkedUser> result = linkedUserRepository.findAllByFeed(feed);

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.stream().map(LinkedUser::getId).toList())
                .containsExactly(l1.getId(), l2.getId(), l3.getId());
    }

    @Test
    @DisplayName(("findFeedsByLinkedUser 테스트"))
    void findFeedsByLinkedUser() {
        // given
        User me = userRepository.save(UserTestFixture.createUser(1L, "me"));
        User other = userRepository.save(UserTestFixture.createUser(2L, "other"));

        LocalDateTime base = LocalDateTime.of(2025, 12, 22, 16, 0);

        Feed myFeed = saveFeedWithCreateDate(
                FeedTestFixture.createFeed(null, me),
                base.plusMinutes(1),
                null
        );
        Feed f1 = saveFeedWithCreateDate(
                FeedTestFixture.createFeed(null, other),
                base.plusMinutes(2),
                null
        );
        Feed deleted = saveFeedWithCreateDate(
                FeedTestFixture.createFeed(null, other),
                base.plusMinutes(3),
                base.plusMinutes(4)
        );

        linkedUserRepository.save(LinkedUserTextFixture.createLinkedUser(null, me, myFeed));
        linkedUserRepository.save(LinkedUserTextFixture.createLinkedUser(null, me, f1));
        linkedUserRepository.save(LinkedUserTextFixture.createLinkedUser(null, me, deleted));

        flushAndClear();

        // when
        Slice<Feed> slice = linkedUserRepository.findFeedsByLinkedUser(me, PageRequest.of(0, 10));

        // then
        assertThat(slice).hasSize(1);
        assertThat(slice.getContent().stream().map(Feed::getId).toList())
                .containsExactly(f1.getId());
    }

    @Test
    @DisplayName("deleteAllByFeed 테스트")
    void deleteAllByFeed() {
        // given
        User author = userRepository.save(UserTestFixture.createUser(1L, "author"));
        User u1 = userRepository.save(UserTestFixture.createUser(2L, "u1"));
        User u2 = userRepository.save(UserTestFixture.createUser(3L, "u2"));

        Feed f1 = feedRepository.save(FeedTestFixture.createFeed(null, author));
        Feed f2 = feedRepository.save(FeedTestFixture.createFeed(null, author));

        linkedUserRepository.save(LinkedUserTextFixture.createLinkedUser(null, u1, f1));
        linkedUserRepository.save(LinkedUserTextFixture.createLinkedUser(null, u1, f2));
        linkedUserRepository.save(LinkedUserTextFixture.createLinkedUser(null, u2, f1));

        flushAndClear();

        // when
        linkedUserRepository.deleteAllByFeed(f1);
        flushAndClear();

        //then
        assertThat(linkedUserRepository.findAllByFeed(f1)).isEmpty();
        assertThat(linkedUserRepository.findAllByFeed(f2)).hasSize(1);
    }

    private Feed saveFeedWithCreateDate(Feed feed, LocalDateTime createDate, LocalDateTime deletedAt) {
        Feed saved = feedRepository.save(feed);
        flushAndClear();

        em.createQuery("UPDATE Feed f SET f.createDate = :createDate, f.deletedAt = :deletedAt WHERE f.id = :id")
                .setParameter("createDate", createDate)
                .setParameter("deletedAt", deletedAt)
                .setParameter("id", saved.getId())
                .executeUpdate();
        flushAndClear();

        return saved;
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
