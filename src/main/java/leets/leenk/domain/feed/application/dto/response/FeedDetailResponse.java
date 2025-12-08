package leets.leenk.domain.feed.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import leets.leenk.domain.media.application.dto.response.FeedMediaResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FeedDetailResponse(
        @Schema(description = "피드 Id", example = "1")
        long feedId,

        @Schema(description = "작성자 정보")
        FeedAuthorResponse author,

        @Schema(description = "피드 설명", example = "행복한 링크 생활")
        String description,

        @Schema(description = "총 공감 수")
        long totalReactionCount,

        @Schema(description = "피드 생성 시간", example = "2025-06-30T00:00:00")
        LocalDateTime createdAt,

        @Schema(description = "피드 미디어 목록")
        List<FeedMediaResponse> media,

        @Schema(description = "함께한 사용자 수 (작성자 포함)", example = "8")
        long linkedUserCount,

        @Schema(description = "함께한 사용자 목록")
        List<LinkedUserResponse> linkedUser,

        @Schema(description = "피드에 작성된 댓글 목록")
        List<GetCommentsResponse> comments
) {
}
