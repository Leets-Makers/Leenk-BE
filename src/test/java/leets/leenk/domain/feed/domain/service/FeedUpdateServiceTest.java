package leets.leenk.domain.feed.domain.service;

import leets.leenk.domain.feed.application.dto.request.FeedUpdateRequest;
import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.Reaction;
import leets.leenk.domain.feed.test.FeedTestFixture;
import leets.leenk.domain.feed.test.ReactionTestFixture;
import leets.leenk.domain.feed.test.UserTestFixture;
import leets.leenk.domain.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FeedUpdateServiceTest {
    private final FeedUpdateService feedUpdateService = new FeedUpdateService();

    @Test
    @DisplayName("feedUpdate 테스트")
    void feedUpdate1() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        Feed feed = FeedTestFixture.createFeed(1L, user);

        FeedUpdateRequest request = new FeedUpdateRequest("\n\nhello\n\n\nworld", null, null);

        // when
        feedUpdateService.update(feed, request);

        // then
        assertThat(feed.getDescription()).isEqualTo("hello\nworld");
    }

    @Test
    @DisplayName("feedUpdate 테스트 - description이 null일 때 유지")
    void feedUpdate2() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        Feed feed = FeedTestFixture.createFeed(1L, user);
        feed.updateDescription("old");

        FeedUpdateRequest request = new FeedUpdateRequest(null, null, null);

        // when
        feedUpdateService.update(feed, request);

        // then
        assertThat(feed.getDescription()).isEqualTo("old");
    }

    @Test
    @DisplayName("updateTotalReaction 테스트")
    void updateTotalReaction() {
        // given
        User user = UserTestFixture.createUser(1L, "me");
        Feed feed = FeedTestFixture.createFeed(1L, user);
        Reaction reaction = ReactionTestFixture.createReaction(feed, user, 2L);

        // when
        feedUpdateService.updateTotalReaction(feed, reaction, user, 3L);

        // then
        assertThat(feed.getTotalReactionCount()).isEqualTo(3L);
        assertThat(reaction.getReactionCount()).isEqualTo(5L);
        assertThat(user.getTotalReactionCount()).isEqualTo(3L);
    }
}
