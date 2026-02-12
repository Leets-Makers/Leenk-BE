package leets.leenk.domain.feed.test

import leets.leenk.domain.feed.domain.entity.Feed
import leets.leenk.domain.media.domain.entity.Media
import leets.leenk.domain.media.domain.entity.enums.MediaType

object MediaTestFixture {
    fun createMedia(
        id: Long,
        feed: Feed,
    ): Media =
        Media
            .builder()
            .id(id)
            .feed(feed)
            .mediaUrl("https://example.com/media.jpg")
            .thumbnailUrl("https://example.com/media.jpg")
            .mediaType(MediaType.IMAGE)
            .position(1)
            .build()
}
