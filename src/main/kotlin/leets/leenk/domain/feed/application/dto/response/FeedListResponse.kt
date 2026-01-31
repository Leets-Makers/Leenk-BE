package leets.leenk.domain.feed.application.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import leets.leenk.global.common.dto.CommonPageableResponse

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FeedListResponse(
    @field:Schema(description = "내가 받은 총 공감 수 (실제 API에서는 /feed/me에서만 반환됩니다. 스웨거 예시 용)", example = "12345")
    val totalReactionCount: Long?,
    @field:Schema(description = "피드 목록")
    val feeds: List<FeedResponse>,
    @field:Schema(description = "페이징 정보")
    val pageable: CommonPageableResponse,
)
