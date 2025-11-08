package leets.leenk.domain.birthday.application.dto.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.v3.oas.annotations.media.Schema;
import leets.leenk.domain.user.application.dto.response.UserProfileResponse;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MyBirthdayLettersResponse(
        @Schema(description = "편지 id", example = "1")
        Long letterId,

        @JsonUnwrapped
        @Schema(implementation = UserProfileResponse.class)
        UserProfileResponse author,

        @Schema(description = "편지 내용", example = "생일 축하한데이")
        String message,

        @Schema(description = "편지 작성 일자", example = "2025-11-30T00:00:00")
        LocalDateTime createdAt
) {
}
