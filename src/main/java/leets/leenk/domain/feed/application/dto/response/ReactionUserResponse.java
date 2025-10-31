package leets.leenk.domain.feed.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReactionUserResponse(
        @Schema(description = "사용자 id", example = "1")
        long userId,

        @Schema(description = "프로필 이미지", example = "https://s3.example.com/profile_image.jpg")
        String profileImage,

        @Schema(description = "사용자 이름", example = "이강혁")
        String name,

        @Schema(description = "사용자 오늘 생일 여부", example = "true")
        Boolean isUserBirthdayToday,

        @Schema(description = "사용자별 공감 개수", example = "10")
        long reactionCount
) {
}
