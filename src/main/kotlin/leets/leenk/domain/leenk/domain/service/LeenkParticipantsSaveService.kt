package leets.leenk.domain.leenk.domain.service

import leets.leenk.domain.leenk.domain.entity.LeenkParticipants
import leets.leenk.domain.leenk.domain.repository.LeenkParticipantsRepository
import org.springframework.stereotype.Service

@Service
class LeenkParticipantsSaveService(
    private val participantsRepository: LeenkParticipantsRepository,
) {
    fun save(participants: LeenkParticipants): LeenkParticipants = participantsRepository.save(participants)
}
