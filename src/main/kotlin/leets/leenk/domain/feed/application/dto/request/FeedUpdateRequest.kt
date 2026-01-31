package leets.leenk.domain.feed.application.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import leets.leenk.domain.media.application.dto.request.FeedMediaRequest

data class FeedUpdateRequest(
    @field:Schema(description = "피드 설명", example = "행복한 링크 생활 (수정할 값만 보내주세요)")
    @field:Size(max = 100)
    val description: String?,
    @field:Valid
    @field:Size(min = 1, max = 3)
    val media: List<FeedMediaRequest>?,
    @field:Schema(description = "함께한 사용자 목록 (수정할 값만 보내주세요)")
    val userIds: List<Long>?,
)
