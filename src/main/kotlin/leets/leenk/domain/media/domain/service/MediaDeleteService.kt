package leets.leenk.domain.media.domain.service

import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.media.domain.entity.Media
import leets.leenk.domain.media.domain.repository.MediaRepository
import org.springframework.stereotype.Service

@Service
class MediaDeleteService(
    private val mediaRepository: MediaRepository,
) {
    fun delete(media: Media) {
        mediaRepository.delete(media)
    }

    fun deleteAll(mediaList: List<Media>) {
        mediaRepository.deleteAll(mediaList)
    }

    fun deleteAllByFeed(feed: Feed) {
        mediaRepository.deleteAllByFeed(feed)
        mediaRepository.flush()
    }
}
