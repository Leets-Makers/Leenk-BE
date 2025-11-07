package leets.leenk.domain.birthday.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpcomingBirthdayUsersResponse(
        @Schema(description = "생일일 유저들")
        List<UpcomingBirthdayUserResponse> users
) {
}
