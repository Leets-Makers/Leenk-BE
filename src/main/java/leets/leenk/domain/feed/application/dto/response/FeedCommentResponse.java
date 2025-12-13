package leets.leenk.domain.feed.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.v3.oas.annotations.media.Schema;
import leets.leenk.domain.user.application.dto.response.UserProfileResponse;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FeedCommentResponse(
        @Schema(description = "댓글 id", example = "1")
        long commentId,

        @JsonUnwrapped
        @Schema(implementation = UserProfileResponse.class)
        UserProfileResponse user,

        @Schema(description = "댓글", example = "오 좋은데??")
        String comment,

        @Schema(description = "댓글 작성 시간", example = "2025-06-30T00:00:00")
        LocalDateTime createDate
) {
}
