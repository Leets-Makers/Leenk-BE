package leets.leenk.domain.leenk.domain.service

import leets.leenk.domain.leenk.domain.entity.Leenk
import leets.leenk.domain.leenk.domain.repository.LeenkRepository
import org.springframework.stereotype.Service

@Service
class LeenkDeleteService(
    private val leenkRepository: LeenkRepository,
) {
    fun delete(leenk: Leenk) {
        leenkRepository.delete(leenk)
    }
}
