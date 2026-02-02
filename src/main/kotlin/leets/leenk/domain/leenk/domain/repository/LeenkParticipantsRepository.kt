package leets.leenk.domain.leenk.domain.repository

import leets.leenk.domain.leenk.domain.entity.Leenk
import leets.leenk.domain.leenk.domain.entity.LeenkParticipants
import leets.leenk.domain.user.domain.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface LeenkParticipantsRepository : JpaRepository<LeenkParticipants, Long> {
    fun findAllByLeenk(leenk: Leenk): List<LeenkParticipants>

    fun findAllByParticipantOrderByJoinedAtDesc(
        user: User,
        pageable: Pageable,
    ): Slice<LeenkParticipants>

    fun existsByLeenkAndParticipant(
        leenk: Leenk,
        user: User,
    ): Boolean

    fun findByLeenkIdAndParticipantId(
        leenkId: Long,
        participantId: Long,
    ): Optional<LeenkParticipants>
}
