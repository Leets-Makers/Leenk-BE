package leets.leenk.domain.feed.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import leets.leenk.domain.media.application.dto.request.FeedMediaRequest;

import java.util.List;

public record FeedUploadRequest(
        @Schema(description = "피드 설명", example = "행복한 링크 생활")
        @Size(max = 100)
        String description,

        @Valid
        @NotNull
        @Size(max = 3)
        List<FeedMediaRequest> media,

        @Schema(description = "함께한 사용자 목록")
        List<Long> userIds
) {
}
