package leets.leenk.domain.leenk.domain.service

import leets.leenk.domain.leenk.domain.entity.LeenkParticipants
import leets.leenk.domain.leenk.domain.repository.LeenkParticipantsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LeenkParticipantsDeleteService(
    private val participantsRepository: LeenkParticipantsRepository,
) {
    @Transactional
    fun delete(participants: LeenkParticipants) {
        participantsRepository.delete(participants)
    }

    @Transactional
    fun deleteAll(participants: List<LeenkParticipants>) {
        participantsRepository.deleteAll(participants)
    }
}
