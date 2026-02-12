package leets.leenk.domain.feed.application.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import leets.leenk.global.common.dto.CommonPageableResponse

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FeedUserListResponse(
    @field:Schema(description = "사용자 목록")
    val users: List<FeedUserResponse>,
    @field:Schema(description = "페이징 정보")
    val pageable: CommonPageableResponse,
)
