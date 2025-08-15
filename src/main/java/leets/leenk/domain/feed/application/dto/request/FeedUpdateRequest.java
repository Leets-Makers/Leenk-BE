package leets.leenk.domain.feed.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import leets.leenk.domain.media.application.dto.request.FeedMediaRequest;

import java.util.List;

public record FeedUpdateRequest(
        @Schema(description = "피드 설명", example = "행복한 링크 생활 (수정할 값만 보내주세요)")
        @Size(max = 100)
        String description,

        @Valid
        @Size(max = 3)
        List<FeedMediaRequest> media,

        @Schema(description = "함께한 사용자 목록 (수정할 값만 보내주세요)")
        List<Long> userIds
) {
}
