package leets.leenk.domain.user.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserProfileResponse(
        @Schema(description = "사용자 id", example = "1")
        long userId,

        @Schema(description = "썸네일", example = "https://s3.example.com/profile_image.jpg")
        String thumbnail,

        @Schema(description = "사용자 이름", example = "이지훈")
        String name,

        @Schema(description = "사용자 오늘 생일 여부", example = "true")
        Boolean isUserBirthdayToday
) {
}
