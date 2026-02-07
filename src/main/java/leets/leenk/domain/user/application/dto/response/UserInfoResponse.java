package leets.leenk.domain.user.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserInfoResponse(
        @JsonUnwrapped
        @Schema(implementation = UserProfileResponse.class)
        UserProfileResponse user,
        Integer cardinal,
        LocalDate birthday,
        String kakaoTalkId,
        String introduction,
        String mbti
) {
}
