package leets.leenk.domain.leenk.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LeenkResponse(

        @Schema(description = "링크 Id", example = "1")
        long leenkId,

        @Schema(description = "유저 Id", example = "1")
        long userId,

        @Schema(description = "링크 제목")
        String title,

        @Schema(description = "현재 참여자 수", example = "2")
        long currentParticipants,

        @Schema(description = "최대 참여자 수", example = "98")
        long maxParticipants,

        @Schema(description = "링크 시작 시간", example = "2025-08-01T10:00:00")
        LocalDateTime startTime,

        @Schema(description = "링크 생성 시간", example = "2025-08-01T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "링크 수정 시간", example = "2025-08-01T12:00:00", nullable = true)
        LocalDateTime updatedAt,

        @Schema(description = "링크 대표 이미지 URL(유저가 업로드한 이미지 중 첫번째)", example = "https://s3.example.com/representative_image.jpg", nullable = true)
        String representativeImage
) {
}
