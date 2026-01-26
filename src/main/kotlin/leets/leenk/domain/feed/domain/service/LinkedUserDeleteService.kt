package leets.leenk.domain.feed.domain.service

import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.repository.LinkedUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LinkedUserDeleteService(
    private val linkedUserRepository: LinkedUserRepository,
) {

    @Transactional
    fun deleteAllByFeed(feed: Feed) {
        linkedUserRepository.deleteAllByFeed(feed)
        linkedUserRepository.flush()
    }
}