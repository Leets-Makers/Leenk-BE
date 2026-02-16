package leets.leenk.domain.media.domain.service

import leets.leenk.domain.media.application.dto.response.MediaUrlResponse
import leets.leenk.domain.media.domain.entity.enums.DomainType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.util.UUID

@Service
class S3PresignedUrlService(
    private val s3Presigner: S3Presigner,
) {
    @Value("\${cloud.aws.s3.bucket}")
    private lateinit var bucket: String

    fun generateUrlList(
        domainType: DomainType,
        fileNames: List<String>,
    ): List<MediaUrlResponse> =
        fileNames.mapIndexed { index, fileName ->
            generateUrl(domainType, fileName, index)
        }

    /*
    차후 CDN 캐싱도 고려
     */
    private fun generateUrl(
        domainType: DomainType,
        fileName: String,
        index: Int,
    ): MediaUrlResponse {
        val key = generateKey(domainType, fileName, index)

        val putObjectRequest =
            PutObjectRequest
                .builder()
                .bucket(bucket)
                .key(key)
                .build()

        val request =
            PutObjectPresignRequest
                .builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectRequest)
                .build()

        val presignedUrlRequest = s3Presigner.presignPutObject(request)
        val putUrl = presignedUrlRequest.url().toString()

        return MediaUrlResponse(fileName, putUrl)
    }

    private fun generateKey(
        domainType: DomainType,
        fileName: String,
        index: Int,
    ): String {
        val key = UUID.randomUUID().toString()
        val extension = fileName.substring(fileName.lastIndexOf('.') + 1)
        val newFileName = "$key.$extension"

        val typePath = domainType.name.lowercase()
        val prefix = if (index == 0) "thumbnail_" else ""

        return "originals/$typePath/$prefix$newFileName"
    }
}
