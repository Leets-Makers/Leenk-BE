package leets.leenk.domain.leenk.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LeenkCreateResponse(

        @Schema(description = "생성된 링크 id", example = "1")
        Long id
) {
}
