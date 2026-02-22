package leets.leenk.domain.media.domain.service

import leets.leenk.domain.media.domain.entity.Media
import leets.leenk.domain.media.domain.repository.MediaRepository
import org.springframework.stereotype.Service

@Service
class MediaSaveService(
    private val mediaRepository: MediaRepository,
) {
    fun save(media: Media) {
        mediaRepository.save(media)
    }

    fun saveAll(mediaList: List<Media>) {
        mediaRepository.saveAll(mediaList)
    }
}
