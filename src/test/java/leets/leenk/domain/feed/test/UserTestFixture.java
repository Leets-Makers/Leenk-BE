package leets.leenk.domain.feed.test;

import leets.leenk.domain.user.domain.entity.User;

public class UserTestFixture {
    public static User createUser(Long id, String name) {
        return User.builder()
                .id(id)
                .name(name)
                .cardinal(1)
                .totalReactionCount(0L)
                .build();
    }
}
