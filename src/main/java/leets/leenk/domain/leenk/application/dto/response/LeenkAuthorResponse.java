package leets.leenk.domain.leenk.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LeenkAuthorResponse(
        @Schema(description = "작성자 id", example = "1")
        long userId,

        @Schema(description = "프로필 이미지", example = "https://s3.example.com/profile_image.jpg")
        String profileImage,

        @Schema(description = "작성자 이름", example = "이지훈")
        String name,

        @Schema(description = "작성자 오늘 생일 여부", example = "true")
        Boolean isAuthorBirthdayToday
) {
}
