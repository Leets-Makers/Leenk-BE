package leets.leenk.domain.media.application.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import leets.leenk.domain.media.domain.entity.enums.MediaType

data class FeedMediaResponse(
    @field:Schema(description = "1부터 시작하는 정수", example = "1")
    val position: Int,

    @field:Schema(description = "미디어 S3 URL", example = "https://s3.example.com/image.jpg")
    val mediaUrl: String,

    @field:Schema(description = "이미지, 비디오", example = "IMAGE or VIDEO")
    val mediaType: MediaType,
)
