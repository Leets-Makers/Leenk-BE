package leets.leenk.domain.leenk.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LeenkParticipatedResponse(

        @Schema(description = "링크 id", example = "1")
        Long id,

        @Schema(description = "작성자 정보")
        LeenkAuthorResponse author,

        @Schema(description = "링크 상태", example = "RECRUITING")
        LeenkStatus status,

        @Schema(description = "제목", example = "전정도에서 번개 고고")
        String title,

        @Schema(description = "링크 일시", example = "2025-08-01T18:00:00")
        LocalDateTime startTime,

        @Schema(description = "현재 참여 인원", example = "3")
        Long currentParticipants,

        @Schema(description = "최대 참여 인원", example = "20")
        Long maxParticipants
) {
}
