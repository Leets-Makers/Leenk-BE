package leets.leenk.domain.media.application.mapper

import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.leenk.domain.entity.Leenk
import leets.leenk.domain.media.application.dto.request.FeedMediaRequest
import leets.leenk.domain.media.domain.entity.Media
import leets.leenk.domain.media.domain.entity.enums.MediaType
import org.springframework.stereotype.Component

@Component
class MediaMapper {
    private companion object {
        const val DEFAULT_POSITION = 1
    }

    fun toMedia(
        feed: Feed,
        request: FeedMediaRequest,
    ): Media =
        Media(
            feed = feed,
            position = request.position(),
            mediaUrl = request.mediaUrl(),
            thumbnailUrl = request.mediaUrl(),
            mediaType = request.mediaType(),
        )

    fun toMedia(
        leenk: Leenk,
        url: String,
    ): Media =
        Media(
            leenk = leenk,
            mediaUrl = url,
            thumbnailUrl = url,
            mediaType = MediaType.IMAGE,
            position = DEFAULT_POSITION,
        )
}
