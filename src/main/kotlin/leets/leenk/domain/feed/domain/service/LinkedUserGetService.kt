package leets.leenk.domain.feed.domain.service

import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.entity.LinkedUser
import leets.leenk.domain.feed.domain.repository.LinkedUserRepository
import leets.leenk.domain.user.domain.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class LinkedUserGetService(
    private val linkedUserRepository: LinkedUserRepository,
) {

    fun findAll(feed: Feed): List<LinkedUser> {
        return linkedUserRepository.findAllByFeed(feed)
    }

    fun findAllByUser(user: User, pageable: Pageable): Slice<Feed> {
        return linkedUserRepository.findFeedsByLinkedUser(user, pageable)
    }
}
