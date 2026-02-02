package leets.leenk.domain.leenk.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LeenkParticipantResponse(

        @Schema(description = "참여자 정보")
        LeenkAuthorResponse participant,

        @Schema(description = "참여자 카카오톡 ID")
        String kakaoTalkId,

        @Schema(description = "현재 참여 인원", example = "3")
        Long currentParticipants,

        @Schema(description = "최대 참여 인원", example = "20")
        Long maxParticipants,

        @Schema(description = "참여 시각")
        LocalDateTime joinedAt,

        @Schema(description = "방장 여부")
        Boolean isHost
) {
}
