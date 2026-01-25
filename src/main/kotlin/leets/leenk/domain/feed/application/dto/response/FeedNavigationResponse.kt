package leets.leenk.domain.feed.application.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "피드 네비게이션 응답 (커서 기반 페이지네이션)")
data class FeedNavigationResponse(
    @field:Schema(description = "현재 피드 상세 정보")
    val current: FeedDetailResponse,

    @field:Schema(description = "이전 피드 목록 (최신순)")
    val prevFeeds: List<FeedDetailResponse>,

    @field:Schema(description = "다음 피드 목록 (최신순)")
    val nextFeeds: List<FeedDetailResponse>,

    @field:Schema(description = "더 이전 피드 존재 여부", example = "true")
    val hasMorePrev: Boolean,

    @field:Schema(description = "더 다음 피드 존재 여부", example = "true")
    val hasMoreNext: Boolean,
)
