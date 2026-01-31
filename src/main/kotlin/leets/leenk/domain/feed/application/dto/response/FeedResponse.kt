package leets.leenk.domain.feed.application.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FeedResponse(
    @field:Schema(description = "피드 Id", example = "1")
    val feedId: Long,
    @field:Schema(description = "작성자 정보")
    val author: FeedAuthorResponse,
    @field:Schema(description = "썸네일 이미지", example = "https://s3.example.com/thumb_nail.jpg")
    val thumbNail: String,
    @field:Schema(description = "총 공감 개수", example = "1004")
    val totalReactionCount: Long,
)
