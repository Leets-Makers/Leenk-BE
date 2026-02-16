package leets.leenk.domain.media.application.usecase

import leets.leenk.domain.media.application.dto.response.MediaUrlResponse
import leets.leenk.domain.media.domain.entity.enums.DomainType
import leets.leenk.domain.media.domain.service.MediaGetService
import leets.leenk.domain.media.domain.service.MediaUpdateService
import leets.leenk.domain.media.domain.service.S3PresignedUrlService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MediaUsecase(
    private val s3PresignedUrlService: S3PresignedUrlService,
    private val mediaGetService: MediaGetService,
    private val mediaUpdateService: MediaUpdateService,
) {
    fun getUrl(
        domainType: DomainType,
        fileName: List<String>,
    ): List<MediaUrlResponse> = s3PresignedUrlService.generateUrlList(domainType, fileName)

    @Transactional
    fun updateThumbnailUrl(
        originalUrl: String,
        thumbnailUrl: String,
    ) {
        val media = mediaGetService.findByMediaUrl(originalUrl)
        mediaUpdateService.update(media, thumbnailUrl)
    }
}
