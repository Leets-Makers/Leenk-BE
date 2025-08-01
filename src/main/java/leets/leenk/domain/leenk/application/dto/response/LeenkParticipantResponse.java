package leets.leenk.domain.leenk.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LeenkParticipantResponse(

        @Schema(description = "링크 id")
        Long id,

        @Schema(description = "참여자 id")
        Long userId,

        @Schema(description = "참여자 이름")
        String userName,

        @Schema(description = "참여 시각")
        LocalDateTime joinedAt,

        @Schema(description = "방장 여부")
        Boolean isHost,

        @Schema(description = "생성일")
        LocalDateTime createdAt,

        @Schema(description = "수정일", nullable = true)
        LocalDateTime updatedAt
) {
}
