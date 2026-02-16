package leets.leenk.domain.media.application.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import leets.leenk.domain.media.domain.entity.enums.MediaType

data class FeedMediaRequest(
    @field:NotNull
    @field:Positive
    @field:Schema(description = "1부터 시작하는 정수")
    val position: Int,
    @field:NotBlank
    @field:Schema(description = "미디어 S3 URL", example = "https://s3.example.com/image.jpg")
    val mediaUrl: String,
    @field:NotNull
    @field:Schema(description = "이미지, 비디오", example = "IMAGE or VIDEO")
    val mediaType: MediaType,
)
