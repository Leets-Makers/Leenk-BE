package leets.leenk.domain.feed.test;

import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.user.domain.entity.User;

import java.time.LocalDateTime;

public class FeedTestFixture {
    public static Feed createFeed(Long id, User author) {
        return Feed.builder()
                .id(id)
                .user(author)
                .description("desc")
                .totalReactionCount(0L)
                .build();
    }

    public static Feed createFeedWithCreateDate(Long id, User user, LocalDateTime createDate) {
        return Feed.builder()
                .id(id)
                .user(user)
                .description("desc")
                .totalReactionCount(0L)
                .createDate(createDate)
                .build();
    }
}
