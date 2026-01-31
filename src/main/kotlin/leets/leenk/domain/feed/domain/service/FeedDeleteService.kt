package leets.leenk.domain.feed.domain.service

import leets.leenk.domain.feed.domain.entity.Feed
import org.springframework.stereotype.Service

@Service
class FeedDeleteService {

    fun delete(feed: Feed) {
        feed.delete()
    }
}
