package leets.leenk.domain.feed.test;

import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.Reaction;
import leets.leenk.domain.user.domain.entity.User;

public class ReactionTestFixture {
    public static Reaction createReaction(Feed feed, User user, long count) {
        return Reaction.builder()
                .feed(feed)
                .user(user)
                .reactionCount(count)
                .build();
    }
}
