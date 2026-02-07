package leets.leenk.domain.feed.domain.repository;

import jakarta.persistence.EntityManager;
import leets.leenk.config.MysqlTestConfig;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.LinkedUser;
import leets.leenk.domain.feed.test.FeedTestFixture;
import leets.leenk.domain.feed.test.LinkedUserTestFixture;
import leets.leenk.domain.feed.test.UserTestFixture;
import leets.leenk.domain.user.domain.entity.User;
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
    LinkedUserRepository linkedUserRepository;

    @Test
    @DisplayName("findAllByFeed 테스트")
    void findAllByFeed() {
        // given
        User author = persistUser("author");
        User u1 = persistUser("u1");
        User u2 = persistUser("u2");
        User u3 = persistUser("u3");

        Feed feed = persistFeed(author);

        LinkedUser l1 = linkedUserRepository.save(LinkedUserTestFixture.createLinkedUser(null, u1, feed));
        LinkedUser l2 = linkedUserRepository.save(LinkedUserTestFixture.createLinkedUser(null, u2, feed));
        LinkedUser l3 = linkedUserRepository.save(LinkedUserTestFixture.createLinkedUser(null, u3, feed));

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
        User me = persistUser("me");
        User other = persistUser("other");

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

        linkedUserRepository.save(LinkedUserTestFixture.createLinkedUser(null, me, myFeed));
        linkedUserRepository.save(LinkedUserTestFixture.createLinkedUser(null, me, f1));
        linkedUserRepository.save(LinkedUserTestFixture.createLinkedUser(null, me, deleted));

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
        User author = persistUser("author");
        User u1 = persistUser("u1");
        User u2 = persistUser("u2");

        Feed f1 = persistFeed(author);
        Feed f2 = persistFeed(author);

        linkedUserRepository.save(LinkedUserTestFixture.createLinkedUser(null, u1, f1));
        linkedUserRepository.save(LinkedUserTestFixture.createLinkedUser(null, u1, f2));
        linkedUserRepository.save(LinkedUserTestFixture.createLinkedUser(null, u2, f1));

        flushAndClear();

        // when
        linkedUserRepository.deleteAllByFeed(f1);
        flushAndClear();

        //then
        assertThat(linkedUserRepository.findAllByFeed(f1)).isEmpty();
        assertThat(linkedUserRepository.findAllByFeed(f2)).hasSize(1);
    }

    private Feed saveFeedWithCreateDate(Feed feed, LocalDateTime createDate, LocalDateTime deletedAt) {
        Feed saved = persistFeed(feed.getUser());
        flushAndClear();

        em.createQuery("UPDATE Feed f SET f.createDate = :createDate, f.deletedAt = :deletedAt WHERE f.id = :id")
                .setParameter("createDate", createDate)
                .setParameter("deletedAt", deletedAt)
                .setParameter("id", saved.getId())
                .executeUpdate();
        flushAndClear();

        return saved;
    }

    private User persistUser(String name) {
        User user = UserTestFixture.createUser(name);
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
