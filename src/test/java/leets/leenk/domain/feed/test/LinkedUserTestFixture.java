package leets.leenk.domain.feed.test;

import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.LinkedUser;
import leets.leenk.domain.user.domain.entity.User;

public class LinkedUserTestFixture {
    public static LinkedUser createLinkedUser(Long id, User user, Feed feed) {
        return LinkedUser.builder()
                .id(id)
                .feed(feed)
                .user(user)
                .build();
    }
}
