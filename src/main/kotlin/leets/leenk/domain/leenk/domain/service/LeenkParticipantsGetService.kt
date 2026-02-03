package leets.leenk.domain.leenk.domain.service

import leets.leenk.domain.leenk.application.exception.LeenkParticipantNotFoundException
import leets.leenk.domain.leenk.domain.entity.Leenk
import leets.leenk.domain.leenk.domain.entity.LeenkParticipants
import leets.leenk.domain.leenk.domain.repository.LeenkParticipantsRepository
import leets.leenk.domain.user.domain.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class LeenkParticipantsGetService(
    private val leenkParticipantsRepository: LeenkParticipantsRepository,
) {
    fun findAllByLeenk(leenk: Leenk): List<LeenkParticipants> =
        leenkParticipantsRepository.findAllByLeenk(leenk)

    fun findSliceByParticipant(user: User, pageable: Pageable): Slice<LeenkParticipants> =
        leenkParticipantsRepository.findAllByParticipantOrderByJoinedAtDesc(user, pageable)

    fun existsByLeenkAndParticipant(leenk: Leenk, user: User): Boolean =
        leenkParticipantsRepository.existsByLeenkAndParticipant(leenk, user)

    fun findByLeenkAndParticipantId(leenkId: Long, participantId: Long): LeenkParticipants =
        leenkParticipantsRepository.findByLeenkIdAndParticipantId(leenkId, participantId)
            .orElseThrow { LeenkParticipantNotFoundException() }
}
