package leets.leenk.domain.feed.application.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import leets.leenk.domain.media.application.dto.request.FeedMediaRequest

data class FeedUploadRequest(
    @field:Schema(description = "피드 설명", example = "행복한 링크 생활")
    @field:Size(max = 100)
    val description: String?,

    @field:Valid
    @field:NotNull
    @field:NotEmpty
    @field:Size(min = 1, max = 3)
    val media: List<FeedMediaRequest>,

    @field:Schema(description = "함께한 사용자 목록")
    val userIds: List<Long>?,
)
