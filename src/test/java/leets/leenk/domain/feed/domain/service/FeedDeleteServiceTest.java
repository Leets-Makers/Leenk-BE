package leets.leenk.domain.feed.domain.service;

import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.test.FeedTestFixture;
import leets.leenk.domain.feed.test.UserTestFixture;
import leets.leenk.domain.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class FeedDeleteServiceTest {
    private final FeedDeleteService feedDeleteService = new FeedDeleteService();

    @Test
    @DisplayName("feedDelete 테스트")
    void feedDelete() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        Feed feed = FeedTestFixture.createFeed(1L, user);

        // when
        LocalDateTime start = LocalDateTime.now();

        feedDeleteService.delete(feed);

        LocalDateTime end = LocalDateTime.now();

        // then
        assertThat(feed.getDeletedAt()).isNotNull();
        assertThat(feed.getDeletedAt()).isAfterOrEqualTo(start);
        assertThat(feed.getDeletedAt()).isBeforeOrEqualTo(end);
    }
}
