package leets.leenk.domain.birthday.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MyBirthdayLettersResponse(
        @Schema(description = "편지 id", example = "1")
        Long letterId,

        @Schema(description = "보낸사람 이름", example = "김가천")
        String name,

        @Schema(description = "사용자의 프로필 이미지", example = "www.image.com/me.jpg")
        String profileImage,

        @Schema(description = "편지 보낸 유저 오늘 생일 여부", example = "true")
        Boolean isSenderBirthdayToday,

        @Schema(description = "편지 내용", example = "생일 축하한데이")
        String message,

        @Schema(description = "편지 작성 일자", example = "2025-11-30T00:00:00")
        LocalDateTime createdAt
) {
}
