package leets.leenk.domain.feed.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "피드 네비게이션 응답 (커서 기반 페이지네이션)")
public record FeedNavigationResponse(
        @Schema(description = "현재 피드 상세 정보")
        FeedDetailResponse current,

        @Schema(description = "이전 피드 목록 (최신순)")
        List<FeedDetailResponse> prevFeeds,

        @Schema(description = "다음 피드 목록 (최신순)")
        List<FeedDetailResponse> nextFeeds,

        @Schema(description = "더 이전 피드 존재 여부", example = "true")
        boolean hasMorePrev,

        @Schema(description = "더 다음 피드 존재 여부", example = "true")
        boolean hasMoreNext
) {
}
