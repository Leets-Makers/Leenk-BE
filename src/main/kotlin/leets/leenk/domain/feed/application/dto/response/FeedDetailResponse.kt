package leets.leenk.domain.feed.application.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import leets.leenk.domain.media.application.dto.response.FeedMediaResponse
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FeedDetailResponse(
    @field:Schema(description = "피드 Id", example = "1")
    val feedId: Long,
    @field:Schema(description = "작성자 정보")
    val author: FeedAuthorResponse,
    @field:Schema(description = "피드 설명", example = "행복한 링크 생활")
    val description: String?,
    @field:Schema(description = "총 공감 수")
    val totalReactionCount: Long,
    @field:Schema(description = "피드 생성 시간", example = "2025-06-30T00:00:00")
    val createdAt: LocalDateTime,
    @field:Schema(description = "피드 미디어 목록")
    val media: List<FeedMediaResponse>,
    @field:Schema(description = "함께한 사용자 수 (작성자 포함)", example = "8")
    val linkedUserCount: Long,
    @field:Schema(description = "함께한 사용자 목록")
    val linkedUser: List<LinkedUserResponse>,
    @field:Schema(description = "피드에 작성된 댓글 목록")
    val comments: List<FeedCommentResponse>,
)
