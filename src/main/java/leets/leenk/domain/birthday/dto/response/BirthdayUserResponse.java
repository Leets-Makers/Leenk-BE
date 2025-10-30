package leets.leenk.domain.birthday.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record BirthdayUserResponse(
        @Schema(description = "사용자 id", example = "1")
        long id,

        @Schema(description = "사용자 이름", example = "김가천")
        String name,

        @Schema(description = "사용자 프로필 이미지", example = "https://www.image.com/me.jpg")
        String profileImage
) {
}
