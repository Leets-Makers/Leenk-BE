package leets.leenk.domain.feed.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetCommentsResponse(
        @Schema(description = "댓글 id", example = "1")
        long commentId,

        @Schema(description = "작성자 id", example = "1")
        long authorId,

        @Schema(description = "작성자 이름", example = "한승현")
        String authorName,

        @Schema(description = "댓글", example = "오 좋은데??")
        String comment
) {
}
