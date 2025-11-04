package leets.leenk.domain.feed.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.v3.oas.annotations.media.Schema;
import leets.leenk.domain.user.application.dto.response.UserProfileResponse;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FeedAuthorResponse(
        @JsonUnwrapped
        @Schema(implementation = UserProfileResponse.class)
        UserProfileResponse author
) {
}
