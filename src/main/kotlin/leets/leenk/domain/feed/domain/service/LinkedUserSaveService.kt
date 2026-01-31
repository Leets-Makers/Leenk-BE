package leets.leenk.domain.feed.domain.service

import leets.leenk.domain.feed.domain.entity.LinkedUser
import leets.leenk.domain.feed.domain.repository.LinkedUserRepository
import org.springframework.stereotype.Service

@Service
class LinkedUserSaveService(
    private val linkedUserRepository: LinkedUserRepository,
) {
    fun save(linkedUser: LinkedUser) {
        linkedUserRepository.save(linkedUser)
    }

    fun saveAll(linkedUsers: List<LinkedUser>) {
        linkedUserRepository.saveAll(linkedUsers)
    }
}
