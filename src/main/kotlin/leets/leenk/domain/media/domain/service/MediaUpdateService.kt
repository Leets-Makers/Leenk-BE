package leets.leenk.domain.media.domain.service

import leets.leenk.domain.media.domain.entity.Media
import org.springframework.stereotype.Service

@Service
class MediaUpdateService {
    fun update(
        media: Media,
        thumbnailUrl: String,
    ) {
        media.updateThumbnailUrl(thumbnailUrl)
    }
}
