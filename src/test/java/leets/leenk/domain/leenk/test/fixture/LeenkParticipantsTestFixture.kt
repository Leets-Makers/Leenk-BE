package leets.leenk.domain.leenk.test.fixture

import leets.leenk.domain.leenk.domain.entity.Leenk
import leets.leenk.domain.leenk.domain.entity.LeenkParticipants
import leets.leenk.domain.user.domain.entity.User
import java.time.LocalDateTime

class LeenkParticipantsTestFixture {
    companion object {
        fun createParticipant(
            id: Long? = null,
            leenk: Leenk,
            participant: User,
            joinedAt: LocalDateTime = LocalDateTime.now()
        ): LeenkParticipants {
            return LeenkParticipants.builder()
                .apply { id?.let { id(it) } }
                .leenk(leenk)
                .participant(participant)
                .joinedAt(joinedAt)
                .build()
        }
    }
}
