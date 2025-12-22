package leets.leenk.domain.feed.domain.repository;

import jakarta.persistence.EntityManager;
import leets.leenk.config.MysqlTestConfig;
import leets.leenk.domain.feed.domain.entity.Feed;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(MysqlTestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FeedRepositoryTest {
    @Autowired
    EntityManager em;

    @Autowired
    FeedRepository feedRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("저장(save) 및 조회(findById)")
    void saveAndFind() {
        User author = UserTestFixture.createUser(1L, "me");
        userRepository.save(author);

        Feed feed = FeedTestFixture.createFeed(null, author);
        Feed saved = feedRepository.save(feed);

        em.flush();
        em.clear();

        Feed result = feedRepository.findById(feed.getId()).orElseThrow();

        assertThat(result.getId()).isEqualTo(saved.getId());
        assertThat(result.getUser().getId()).isEqualTo(author.getId());
        assertThat(result.getDescription()).isEqualTo("desc");
        assertThat(result.getTotalReactionCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("findAllByDeletedAtIsNullWithUser 테스트")
    void findAllByDeletedAtIsNullWithUser() {
        // given
        User u1 = userRepository.save(UserTestFixture.createUser(1L, "me"));
        User u2 = userRepository.save(UserTestFixture.createUser(2L, "me2"));

        LocalDateTime base = LocalDateTime.of(2025, 12, 22, 15, 0);

        Feed f1 = feedRepository.save(FeedTestFixture.createFeed(null, u1));
        Feed f2 = feedRepository.save(FeedTestFixture.createFeed(null, u1));
        Feed f3 = feedRepository.save(FeedTestFixture.createFeed(null, u1));
        Feed block = feedRepository.save(FeedTestFixture.createFeed(null, u2));
        Feed delete = feedRepository.save(FeedTestFixture.createFeed(null, u1));

        flushAndClear();

        updateFeedDates(f1.getId(), base.plusMinutes(1), null);
        updateFeedDates(f2.getId(), base.plusMinutes(2), null);
        updateFeedDates(f3.getId(), base.plusMinutes(3), null);
        updateFeedDates(block.getId(), base.plusMinutes(4), null);
        updateFeedDates(delete.getId(), base.plusMinutes(5), base.plusMinutes(6));

        flushAndClear();

        // when
        Slice<Feed> slice = feedRepository.findAllByDeletedAtIsNullWithUser(
                PageRequest.of(0, 2),
                List.of(u2.getId())
        );

        // then
        assertThat(slice.getContent()).hasSize(2);
        assertThat(slice.hasNext()).isTrue();

        List<Long> ids = slice.getContent().stream().map(Feed::getId).toList();
        assertThat(ids).containsExactly(f3.getId(), f2.getId());

        assertThat(slice.getContent()).allSatisfy(feed -> {
            assertThat(feed.getDeletedAt()).isNull();
            assertThat(feed.getUser().getId()).isNotEqualTo(u2.getId());
        });
    }

    @Test
    @DisplayName("findAllByUserAndDeletedAtIsNull 테스트")
    void findAllByUserAndDeletedAtIsNull() {
        //given
        User me = userRepository.save(UserTestFixture.createUser(3L, "me"));
        User other = userRepository.save(UserTestFixture.createUser(4L, "me2"));

        LocalDateTime base = LocalDateTime.of(2025, 12, 22, 16, 0);

        Feed mine1 = feedRepository.save(FeedTestFixture.createFeed(null, me));
        Feed mine2 = feedRepository.save(FeedTestFixture.createFeed(null, me));
        Feed mineDeleted = feedRepository.save(FeedTestFixture.createFeed(null, me));
        Feed other1 = feedRepository.save(FeedTestFixture.createFeed(null, other));

        flushAndClear();

        updateFeedDates(mine2.getId(), base.plusMinutes(1), null);
        updateFeedDates(mine2.getId(), base.plusMinutes(2), null);
        updateFeedDates(mineDeleted.getId(), base.plusMinutes(3), base.plusMinutes(4));
        updateFeedDates(other1.getId(), base.plusMinutes(4), null);

        flushAndClear();

        // when
        Slice<Feed> slice = feedRepository.findAllByUserAndDeletedAtIsNull(me, PageRequest.of(0, 10));

        // then
        assertThat(slice.getContent()).hasSize(2);
        assertThat(slice.getContent().stream().map(Feed::getId).toList())
                .containsExactly(mine2.getId(), mine1.getId());
        assertThat(slice.getContent()).allSatisfy(feed -> {
            assertThat(feed.getUser().getName()).isEqualTo(me.getName());
            assertThat(feed.getDeletedAt()).isNull();
        });
    }

    @Test
    @DisplayName("findByDeletedAtIsNullAndId 테스트")
    void findByDeletedAtIsNullAndId() {
        // given
        User me = userRepository.save(UserTestFixture.createUser(31L, "me"));

        LocalDateTime base = LocalDateTime.of(2025, 12, 22, 16, 0);

        Feed notDeleted = feedRepository.save(FeedTestFixture.createFeed(null, me));
        Feed Deleted = feedRepository.save(FeedTestFixture.createFeed(null, me));

        flushAndClear();

        updateFeedDates(Deleted.getId(), base.plusMinutes(1), base.plusMinutes(2));
        flushAndClear();

        // when
        Optional<Feed> notDeletedFeed = feedRepository.findByDeletedAtIsNullAndId(notDeleted.getId());
        Optional<Feed> deletedFeed = feedRepository.findByDeletedAtIsNullAndId(Deleted.getId());

        // then
        assertThat(notDeletedFeed).isPresent();
        assertThat(notDeletedFeed.get().getId()).isEqualTo(notDeleted.getId());
        assertThat(deletedFeed).isEmpty();
    }

    @Test
    @DisplayName("findPrevFeeds 테스트")
    void findPrevFeeds() {
        // given
        User me = userRepository.save(UserTestFixture.createUser(5L, "me"));
        User blockedUser = userRepository.save(UserTestFixture.createUser(6L, "blocked"));

        LocalDateTime base = LocalDateTime.of(2025, 12, 22, 16, 0);

        Feed feed1 = feedRepository.save(FeedTestFixture.createFeed(null, me));
        Feed feed2 = feedRepository.save(FeedTestFixture.createFeed(null, me));
        Feed feedDeleted = feedRepository.save(FeedTestFixture.createFeed(null, me));
        Feed feed3 = feedRepository.save(FeedTestFixture.createFeed(null, me));
        Feed feedBlocked = feedRepository.save(FeedTestFixture.createFeed(null, blockedUser));


        flushAndClear();

        updateFeedDates(feed1.getId(), base.plusMinutes(1), null);
        updateFeedDates(feed2.getId(), base.plusMinutes(1), null);
        updateFeedDates(feedDeleted.getId(), base.plusMinutes(2), base.plusMinutes(4));
        updateFeedDates(feed3.getId(), base.plusMinutes(3), null);
        updateFeedDates(feedBlocked.getId(), base.plusMinutes(4), null);

        flushAndClear();

        // when
        List<Feed> prevFeeds = feedRepository.findPrevFeeds(
                base,
                List.of(blockedUser.getId()),
                PageRequest.of(0, 2)
        );

        // then
        assertThat(prevFeeds).hasSize(2);
        assertThat(prevFeeds.stream().map(Feed::getId).toList())
                .containsExactly(feed1.getId(), feed2.getId());
        assertThat(prevFeeds).allSatisfy(feed -> {
            assertThat(feed.getCreateDate()).isAfter(base);
            assertThat(feed.getDeletedAt()).isNull();
            assertThat(feed.getUser().getId()).isNotEqualTo(blockedUser.getId());
        });
    }

    @Test
    @DisplayName("findNextFeeds 테스트")
    void findNextFeeds(){
        // given
        User me = userRepository.save(UserTestFixture.createUser(5L, "me"));
        User blockedUser = userRepository.save(UserTestFixture.createUser(6L, "blocked"));

        LocalDateTime base = LocalDateTime.of(2025, 12, 22, 16, 0);

        Feed feed1 = feedRepository.save(FeedTestFixture.createFeed(null, me));
        Feed feed2 = feedRepository.save(FeedTestFixture.createFeed(null, me));
        Feed feedDeleted = feedRepository.save(FeedTestFixture.createFeed(null, me));
        Feed feedBlocked = feedRepository.save(FeedTestFixture.createFeed(null, blockedUser));
        Feed feed3 = feedRepository.save(FeedTestFixture.createFeed(null, me));


        flushAndClear();

        updateFeedDates(feed1.getId(), base.minusMinutes(10), null);
        updateFeedDates(feed2.getId(), base.minusMinutes(20), null);
        updateFeedDates(feedDeleted.getId(), base.minusMinutes(30), base.minusMinutes(29));
        updateFeedDates(feedBlocked.getId(), base.minusMinutes(40), null);
        updateFeedDates(feed3.getId(), base.minusMinutes(10), null);

        flushAndClear();

        // when
        List<Feed> nextFeeds = feedRepository.findNextFeeds(
                base,
                List.of(blockedUser.getId()),
                PageRequest.of(0, 2)
        );

        // then
        assertThat(nextFeeds).hasSize(2);
        assertThat(nextFeeds.stream().map(Feed::getId).toList())
                .containsExactly(feed1.getId(), feed3.getId());
        assertThat(nextFeeds).allSatisfy(feed -> {
            assertThat(feed.getCreateDate()).isBefore(base);
            assertThat(feed.getDeletedAt()).isNull();
            assertThat(feed.getUser().getId()).isNotEqualTo(blockedUser.getId());
        });
    }

    private void updateFeedDates(Long feedId, LocalDateTime createdDate, LocalDateTime deletedAt) {
        em.createQuery("UPDATE Feed f SET f.createDate = :createDate, f.deletedAt = :deletedAt WHERE f.id = :id")
                .setParameter("createDate", createdDate)
                .setParameter("deletedAt", deletedAt)
                .setParameter("id", feedId)
                .executeUpdate();
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
