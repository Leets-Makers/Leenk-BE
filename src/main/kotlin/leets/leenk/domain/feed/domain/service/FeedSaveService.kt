package leets.leenk.domain.feed.domain.service

import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.feed.domain.repository.FeedRepository
import org.springframework.stereotype.Service

@Service
class FeedSaveService(
    private val feedRepository: FeedRepository,
) {

    fun save(feed: Feed) {
        feedRepository.save(feed)
    }
}
