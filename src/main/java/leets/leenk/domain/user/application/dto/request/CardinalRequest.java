package leets.leenk.domain.user.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CardinalRequest(
        @NotNull(message = "기수는 필수입니다")
        @Min(value = 1, message = "기수는 1 이상이어야 합니다")
        @Schema(description = "수정할 기수", example = "5")
        Integer cardinal
) {
}
