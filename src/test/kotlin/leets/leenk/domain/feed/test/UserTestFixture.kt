package leets.leenk.domain.feed.test

import leets.leenk.domain.user.domain.entity.User

object UserTestFixture {
    fun createUser(
        id: Long,
        name: String,
    ): User =
        User
            .builder()
            .id(id)
            .name(name)
            .cardinal(1)
            .totalReactionCount(0L)
            .build()
}
