package leets.leenk.domain.media.test.fixture

import leets.leenk.domain.media.domain.entity.Media
import leets.leenk.domain.media.domain.entity.enums.MediaType

class MediaTestFixture {
    companion object {
        fun createMedia(
            id: Long = 1L,
            mediaUrl: String = "https://example.com/media.jpg",
            thumbnailUrl: String = "https://example.com/thumbnail.jpg",
            mediaType: MediaType = MediaType.IMAGE,
            position: Int = 0,
        ): Media =
            Media(
                id = id,
                mediaUrl = mediaUrl,
                thumbnailUrl = thumbnailUrl,
                mediaType = mediaType,
                position = position,
            )
    }
}
