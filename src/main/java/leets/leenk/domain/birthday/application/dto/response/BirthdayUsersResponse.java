package leets.leenk.domain.birthday.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import leets.leenk.domain.user.application.dto.response.UserProfileResponse;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BirthdayUsersResponse(
        @Schema(description = "생일인 유저들")
        List<UserProfileResponse> users,

        @Schema(description = "받은 편지 개수", example = "100")
        Long myBirthdayLettersCounts
) {
}
