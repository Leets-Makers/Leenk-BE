package leets.leenk.domain.leenk.domain.service

import leets.leenk.domain.leenk.domain.entity.Leenk
import leets.leenk.domain.leenk.domain.repository.LeenkRepository
import org.springframework.stereotype.Service

@Service
class LeenkSaveService(
    private val leenkRepository: LeenkRepository,
) {
    fun save(leenk: Leenk) {
        leenkRepository.save(leenk)
    }
}
