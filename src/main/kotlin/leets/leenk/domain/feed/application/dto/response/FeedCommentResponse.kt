package leets.leenk.domain.feed.application.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonUnwrapped
import io.swagger.v3.oas.annotations.media.Schema
import leets.leenk.domain.user.application.dto.response.UserProfileResponse
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FeedCommentResponse(
    @field:Schema(description = "댓글 id", example = "1")
    val commentId: Long,

    @field:JsonUnwrapped
    @field:Schema(implementation = UserProfileResponse::class)
    val user: UserProfileResponse,

    @field:Schema(description = "댓글", example = "오 좋은데??")
    val comment: String,

    @field:Schema(description = "댓글 작성 시간", example = "2025-06-30T00:00:00")
    val createdAt: LocalDateTime,
)
