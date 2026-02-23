package leets.leenk.domain.media.domain.service

import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.leenk.domain.entity.Leenk
import leets.leenk.domain.media.application.exception.MediaNotFoundException
import leets.leenk.domain.media.domain.entity.Media
import leets.leenk.domain.media.domain.repository.MediaRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class MediaGetService(
    private val mediaRepository: MediaRepository,
) {
    fun findById(mediaId: Long): Media =
        mediaRepository
            .findById(mediaId)
            .orElseThrow(::MediaNotFoundException)

    fun findAllByFeed(feed: Feed): List<Media> = mediaRepository.findAllByFeedOrderByPosition(feed)

    fun findAllByFeeds(feeds: List<Feed>): List<Media> = mediaRepository.findAllByFeedInOrderByPosition(feeds)

    fun findFirstMediaByLeenk(leenk: Leenk): Optional<Media> = mediaRepository.findFirstByLeenkOrderByPositionAsc(leenk)

    fun findMediaUrlByLeenk(leenk: Leenk): String =
        mediaRepository
            .findFirstByLeenkOrderByPositionAsc(leenk)
            .map { it.mediaUrl }
            .orElse("")

    fun findByLeenks(leenks: List<Leenk>): List<Media> = mediaRepository.findAllByLeenkInOrderByPosition(leenks)

    fun findByMediaUrl(originalUrl: String): Media =
        mediaRepository
            .findByMediaUrl(originalUrl)
            .orElseThrow(::MediaNotFoundException)
}
